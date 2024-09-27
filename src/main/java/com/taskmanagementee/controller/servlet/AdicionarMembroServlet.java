package com.taskmanagementee.controller.servlet;

import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.DAO.MembroDAO;
import com.taskmanagementee.model.Usuario;
import com.taskmanagementee.model.DAO.UsuarioDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/gestor/adicionarMembro")
public class AdicionarMembroServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;
    private MembroDAO membroDAO;
    private Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            connection = DatabaseConnection.getConnection();
            usuarioDAO = new UsuarioDAO(connection);
            membroDAO = new MembroDAO(connection);
        } catch (SQLException e) {
            throw new ServletException("Não foi possível inicializar o DAO", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        // Verificar se o usuário está logado
        if (session.getAttribute("usuario") == null) {
            response.sendRedirect("/index.jsp");
            return;
        }

        // Captura de parâmetros
        String id = request.getParameter("id");
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String confirmarSenha = request.getParameter("confirmarSenha");
        String funcao = request.getParameter("funcao");
        String projeto = request.getParameter("projeto");

        try {
            // Lógica para adicionar ou atualizar membro
            if (id == null || id.isEmpty()) {
                if (senha != null && confirmarSenha != null && senha.equals(confirmarSenha)) {
                    membroDAO.addMembro(nome, email, senha, projeto, funcao);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"message\": \"Membro adicionado com sucesso!\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"message\": \"As senhas não coincidem.\"}");
                }
            } else {
                int membroId = Integer.parseInt(id);
                Usuario usuarioExistente = usuarioDAO.buscarPorId(membroId);

                if (usuarioExistente != null) {
                    usuarioExistente.setNome(nome);
                    usuarioExistente.setEmail(email);

                    if (senha != null && !senha.isEmpty() && senha.equals(confirmarSenha)) {
                        usuarioExistente.setSenha(senha);  // Atualiza a senha se fornecida
                    }

                    membroDAO.updateMembro(usuarioExistente, projeto, funcao);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"message\": \"Membro atualizado com sucesso!\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"message\": \"Membro não encontrado.\"}");

                }
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Erro ao salvar membro.\"}");
        }

    }
}
