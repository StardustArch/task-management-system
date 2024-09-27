package com.taskmanagementee.controller.servlet;

import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.DAO.ProjectoDAO;
import com.taskmanagementee.model.DAO.TarefaDAO;
import com.taskmanagementee.model.DAO.UsuarioDAO;
import com.taskmanagementee.model.Tarefa;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/gestor/excluirTarefa")
public class ExcluirTarefaServlet extends HttpServlet {

    private TarefaDAO tarefaDAO;
    private UsuarioDAO usuarioDAO;
    private ProjectoDAO projectoDAO;

    @Override
    public void init() throws ServletException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        tarefaDAO = new TarefaDAO(conn);
        usuarioDAO = new UsuarioDAO(conn);
        projectoDAO = new ProjectoDAO(conn);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Recupera o ID da tarefa da URL
        String idParam = request.getParameter("id");

        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);

                // Busca a tarefa pelo ID

                Tarefa tarefa = tarefaDAO.getTarefaById(id);

                if (tarefa != null) {
                    // Exclui a tarefa
                    tarefaDAO.deleteTarefa(tarefa.getId());

                    // Redireciona para a página de gerenciamento de tarefas
                    response.sendRedirect("tarefas");
                } else {
                    // Tarefa não encontrada
                    response.sendRedirect("tarefas?error=Tarefa+nao+encontrada");
                }
            } catch (NumberFormatException e) {
                // ID inválido
                response.sendRedirect("tarefas?error=ID+invalido");
            }
        } else {
            // Se o ID não estiver presente
            response.sendRedirect("tarefas?error=ID+nao+fornecido");
        }
    }
}

