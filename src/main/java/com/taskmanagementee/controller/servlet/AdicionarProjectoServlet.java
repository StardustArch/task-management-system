package com.taskmanagementee.controller.servlet;

import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.Projecto;
import com.taskmanagementee.model.DAO.ProjectoDAO;

import com.taskmanagementee.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
@WebServlet("/gestor/adicionarProjecto")
public class AdicionarProjectoServlet extends HttpServlet {

    private ProjectoDAO projectoDAO;
    private Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        projectoDAO = new ProjectoDAO(connection);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        if (session.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        String id = request.getParameter("id");
        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        String prazo = request.getParameter("prazo");
        System.out.println(id+" "+nome+" "+descricao+" "+prazo);

        try {
            if (id == null || id.isEmpty()) {
                Projecto novoProjecto = new Projecto();
                novoProjecto.setNome(nome);
                novoProjecto.setDescricao(descricao);
                novoProjecto.setPrazo(Date.valueOf(prazo).toLocalDate());
                novoProjecto.setGestorId(usuario.getId());

                projectoDAO.salvarProjeto(novoProjecto);
            } else {
                int projectId = Integer.parseInt(id);
                Projecto projectoExistente = projectoDAO.buscarPorId(projectId);

                if (projectoExistente != null && projectoExistente.getGestorId() == usuario.getId()) {
                    projectoExistente.setNome(nome);
                    projectoExistente.setDescricao(descricao);
                    projectoExistente.setPrazo(Date.valueOf(prazo).toLocalDate());

                    projectoDAO.atualizarProjeto(projectoExistente);
                }
            }

            // Responder com JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\": \"Projeto salvo com sucesso!\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Erro ao salvar projeto.\"}");
        }
    }
}
