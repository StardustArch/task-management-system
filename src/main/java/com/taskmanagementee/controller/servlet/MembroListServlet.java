package com.taskmanagementee.controller.servlet;

import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.DAO.MembroDAO;
import com.taskmanagementee.model.DAO.ProjectoDAO;
import com.taskmanagementee.model.DAO.UsuarioDAO;
import com.taskmanagementee.model.Projecto;
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

@WebServlet("/gestor/membros")
public class MembroListServlet extends HttpServlet {

//    private UsuarioDAO usuarioDAO;
private MembroDAO membroDAO;
private ProjectoDAO projectoDAO;
private Usuario usuario;
    @Override
    public void init() throws ServletException {
        // Inicializa a conexão e o DAO
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        membroDAO = new MembroDAO(conn);
        projectoDAO = new ProjectoDAO(conn);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtém a sessão do usuário
        HttpSession session = request.getSession();
        if (session.getAttribute("usuario") == null) {
            response.sendRedirect("/index.jsp");
            return;
        }

        usuario = (Usuario) session.getAttribute("usuario");

        try {
            // Busca todos os membros
            List<Usuario> usuarios = membroDAO.getAllMembers();

            // Define os membros como atributo da requisição
            request.setAttribute("usuarios", usuarios);
            request.setAttribute("roles", loadRoles()); // Carregar funções
            request.setAttribute("projetos", getProjetos()); // Carregar projetos do banco

            // Encaminha a requisição para a página JSP de listagem de membros
            request.getRequestDispatcher("listarMembros.jsp").forward(request, response);

        } catch (Exception e) {
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


}