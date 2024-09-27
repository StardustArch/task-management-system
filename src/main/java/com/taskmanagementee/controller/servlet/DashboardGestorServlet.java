package com.taskmanagementee.controller.servlet;

import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.DAO.TarefaDAO;
import com.taskmanagementee.model.DAO.ProjectoDAO;
import com.taskmanagementee.model.DAO.MembroDAO;
import com.taskmanagementee.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/gestor/dashboard")
public class DashboardGestorServlet extends HttpServlet {

    private ProjectoDAO projectoDAO;
    private MembroDAO membroDAO;
    private TarefaDAO tarefaDAO;
    private Connection conn;
    private Usuario usuario;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // Testa a conexão com o banco de dados durante a inicialização do servlet
            conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                membroDAO = new MembroDAO(conn);
                tarefaDAO = new TarefaDAO(conn);
                projectoDAO = new ProjectoDAO(conn);
                System.out.println("Conexão com o banco de dados estabelecida com sucesso durante init.");
            } else {
                System.err.println("Não foi possível estabelecer a conexão com o banco de dados durante init.");
            }
        } catch (SQLException e) {
            throw new ServletException("Erro ao conectar ao banco de dados durante init", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null || !"Gestor".equals(usuario.getPapel())) {
            // Redireciona para a página de login se o usuário não estiver autenticado ou não for um gestor
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            // Testa a conexão com o banco de dados
            if (conn != null && !conn.isClosed()) {
                System.out.println("Conexão com o banco de dados estabelecida com sucesso durante doGet.");
            } else {
                throw new ServletException("Não foi possível estabelecer a conexão com o banco de dados durante doGet.");
            }
            request.setAttribute("roles", loadRoles()); // Carregar funções
            request.setAttribute("projetos", getProjetos()); // Carregar projetos do banco
//            request.setAttribute("projetos", getProjetos()); // Carregar projetos do banco
            request.setAttribute("responsaveis", getResponsaveis()); // Carregar projetos do banco

            // Obtém os dados de estatísticas
            int totalMembros = membroDAO.getTotalMembros();
            int totalProjetosAtivos = projectoDAO.getTotalProjetosAtivos(usuario.getId());
            int totalTarefasPendentes = tarefaDAO.getTotalTarefasPendentes(usuario.getId());

            // Define os atributos na requisição
            request.setAttribute("totalMembros", totalMembros >= 0 ? totalMembros : "Erro ao obter total de membros");
            request.setAttribute("totalProjetosAtivos", totalProjetosAtivos >= 0 ? totalProjetosAtivos : "Erro ao obter total de projetos ativos");
            request.setAttribute("totalTarefasPendentes", totalTarefasPendentes >= 0 ? totalTarefasPendentes : "Erro ao obter total de tarefas pendentes");

            // Encaminha a requisição para o JSP do dashboard
            request.getRequestDispatcher("/gestor/dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("erro.jsp");
        }


    }

    public List<String> loadRoles() {
        return Arrays.asList(
                "Gerente de Projeto",
                "Coordenador de Projeto",
                "Desenvolvedor",
                "Analista de Requisitos",
                "Designer",
                "Testador/QA",
                "Documentador",
                "Especialista em Suporte",
                "Consultor",
                "Analista de Dados"
        );
    }


    public List<String> getProjetos() {

        return projectoDAO.buscarNomesProjetosPorGestor(usuario.getId());
    }
    public List<String> getResponsaveis(){
        return membroDAO.buscarNomesMembros();
    }
}
