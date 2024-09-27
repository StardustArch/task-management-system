package com.taskmanagementee.controller.servlet;

import com.taskmanagementee.model.DAO.*;
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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // Testa a conexão com o banco de dados durante a inicialização do servlet
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                usuarioDAO = new UsuarioDAO(conn);

                System.out.println("Conexão com o banco de dados estabelecida com sucesso durante init.");
            } else {
                System.err.println("Não foi possível estabelecer a conexão com o banco de dados durante init.");
            }
        } catch (SQLException e) {
            throw new ServletException("Erro ao conectar ao banco de dados durante init", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Obtém os parâmetros do formulário
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        // Autentica o usuário usando o DAO
        Usuario usuario = usuarioDAO.autenticarUsuario(email, senha);

        if (usuario != null) {
            System.out.println("Usuário autenticado: " + usuario.getEmail());
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario);

            if ("Gestor".equals(usuario.getPapel())) {
                response.sendRedirect("gestor/dashboard");
            } else if ("Membro".equals(usuario.getPapel())) {
                response.sendRedirect("membro/dashboard");
            } else {
                response.sendRedirect("index.jsp?error=invalidRole");
            }
        } else {
            System.out.println("Autenticação falhou para o usuário com email: " + email);
            response.sendRedirect("index.jsp?error=invalidCredentials");
        }
    }
}

