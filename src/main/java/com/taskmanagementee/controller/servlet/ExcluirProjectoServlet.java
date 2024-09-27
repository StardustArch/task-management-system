package com.taskmanagementee.controller.servlet;

import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.DAO.ProjectoDAO;
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

@WebServlet("/gestor/excluirProjecto")
public class ExcluirProjectoServlet extends HttpServlet {

    private ProjectoDAO projectoDAO;
    private Connection connection;

    @Override
    public void init() throws ServletException {
        // Inicializar o DAO (certifique-se de que você tenha a classe ProjectoDAO para interagir com o banco de dados)
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        projectoDAO = new ProjectoDAO(connection);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        // Verifica se o usuário está logado
        if (session.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Obtém o ID do projeto a ser excluído
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int projectId = Integer.parseInt(idStr);

            // Busca o projeto para garantir que ele existe e pertence ao usuário
            Projecto projecto = projectoDAO.buscarPorId(projectId);
            if (projecto != null && projecto.getGestorId() == usuario.getId()) {
                // Remove o projeto
                projectoDAO.excluirProjeto(projectId);
            }
        }

        // Redireciona para a lista de projetos
        response.sendRedirect("projectos");
    }
}

