package com.taskmanagementee.controller.servlet;

import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.Projecto;
import com.taskmanagementee.model.Tarefa;
import com.taskmanagementee.model.DAO.ProjectoDAO;
import com.taskmanagementee.model.DAO.TarefaDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/gestor/detalhesProjecto")
public class DetalhesProjectoServlet extends HttpServlet {
    private ProjectoDAO projectoDAO;
    private TarefaDAO tarefaDAO;
    private Connection conn;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // Testa a conexão com o banco de dados durante a inicialização do servlet
            conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String projectIdParam = request.getParameter("id");

        if (projectIdParam != null) {
            try {
                int projectId = Integer.parseInt(projectIdParam);
                Projecto projecto = projectoDAO.buscarPorId2(projectId);
                List<Tarefa> tarefas = tarefaDAO.getTarefasByProjetoId(projectId);

                // Calcula a porcentagem de conclusão
                int totalTarefas = tarefas.size();
                int tarefasConcluidas = (int) tarefas.stream().filter(t -> "Concluida".equals(t.getStatus())).count();
                int tarefasPendentes = (int) tarefas.stream().filter(t -> "Pendente".equals(t.getStatus())).count();
                int tarefasAtrasadas = (int) tarefas.stream().filter(t -> "Atrasada".equals(t.getStatus())).count();
                int tarefasAndamento = totalTarefas - tarefasConcluidas - tarefasPendentes - tarefasAtrasadas;

                int porcentagemConclusao = totalTarefas > 0 ? (tarefasConcluidas * 100) / totalTarefas : 0;

                // Obtemos data de criação e última atualização
                String dataCriacao = projecto.getData_criacao().toString(); // método que você deve implementar
                String ultimaAtualizacao = projectoDAO.getUltimaAtualizacao(projectId).toString(); // método que você deve implementar

                // Adiciona atributos à requisição
                request.setAttribute("projecto", projecto);
                request.setAttribute("tarefas", tarefas);
                request.setAttribute("porcentagemConclusao", porcentagemConclusao);
                request.setAttribute("dataCriacao", dataCriacao);
                request.setAttribute("ultimaAtualizacao", ultimaAtualizacao);
                request.setAttribute("tarefasPendentes", tarefasPendentes);
                request.setAttribute("tarefasAndamento", tarefasAndamento);
                request.setAttribute("tarefasConcluidas", tarefasConcluidas);
                request.setAttribute("tarefasAtrasadas", tarefasAtrasadas);
                request.setAttribute("totalTarefas", totalTarefas);

                // Redireciona para a página de detalhes do projeto
                request.getRequestDispatcher("detalhesProjecto.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do projeto inválido.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do projeto não fornecido.");
        }
    }
}
