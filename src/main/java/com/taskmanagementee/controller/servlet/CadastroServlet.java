package com.taskmanagementee.controller.servlet;

import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.DAO.UsuarioDAO;
import com.taskmanagementee.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    private Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            connection = DatabaseConnection.getConnection();
            usuarioDAO = new UsuarioDAO(connection);
        } catch (Exception e) {
            throw new ServletException("Erro ao inicializar DAOs", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Coletando os dados do formulário
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String confirmaSenha = request.getParameter("confirmaSenha");
        String papel = request.getParameter("papel");

        // Verifica se todos os campos foram preenchidos
        if (nome == null || email == null || senha == null || confirmaSenha == null || papel == null) {
            response.sendRedirect("cadastro.jsp?error=missingFields");
            return;
        }

        // Verifica se as senhas coincidem
        if (!senha.equals(confirmaSenha)) {
            response.sendRedirect("cadastro.jsp?error=passwordMismatch");
            return;
        }

        // Verificar se o email já existe no banco de dados
        if (usuarioDAO.verificarEmailExistente(email)) {
            response.sendRedirect("cadastro.jsp?error=emailExists");
            return;
        }

        // Criando um novo usuário
        Usuario novoUsuario = new Usuario(nome, email, senha, papel);

        // Inserir o novo usuário no banco de dados
        boolean sucesso = usuarioDAO.inserirUsuario(novoUsuario);

        if (sucesso) {
            // Redireciona para a tela de login após o cadastro bem-sucedido
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("<script>alert('Cadastor efectuado com sucesso!')</script>");
            response.sendRedirect("index.jsp");
        } else {
            // Em caso de erro, redireciona para a tela de cadastro com mensagem de erro
            response.sendRedirect("cadastro.jsp?error=failed");
        }
    }
}
