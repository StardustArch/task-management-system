package com.taskmanagementee.controller.servlet;


import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.DAO.ProjectoDAO;
import com.taskmanagementee.model.DAO.TarefaDAO;
import com.taskmanagementee.model.DAO.UsuarioDAO;
import com.taskmanagementee.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/gestor/atualizarPerfil")
public class AtualizarPerfilServlet extends HttpServlet {


    private Connection connection;
    private UsuarioDAO usuarioDAO;

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

        // Obtém os parâmetros do formulário
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String novaSenha = request.getParameter("novaSenha");

        // Obtém o usuário da sessão
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Verifica se o usuário está logado
        if (usuario == null) {
            // Usuário não autenticado, redireciona para a página de login
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Instância do DAO para interagir com o banco de dados


        try {
            // Verifica se a senha atual está correta
            // Atualiza os dados do usuário
            usuario.setNome(nome);
            usuario.setEmail(email);

            // Verifica se uma nova senha foi fornecida
            boolean atualizarSenha = (novaSenha != null && !novaSenha.trim().isEmpty());

            if (atualizarSenha) {
                // Se houver uma nova senha, ela será atualizada
                usuario.setSenha(novaSenha);
            }

            // Salva as alterações no banco de dados
            usuarioDAO.atualizarUsuario(usuario, atualizarSenha);

            // Atualiza o usuário na sessão
            session.setAttribute("usuario", usuario);

            // Redireciona de volta para o painel com mensagem de sucesso
            request.setAttribute("mensagemSucesso", "Perfil atualizado com sucesso.");
            response.sendRedirect(request.getContextPath() + "/gestor/dashboard");

        } catch (Exception e) {
            e.printStackTrace();
            // Em caso de erro, redireciona para a página com uma mensagem de erro
            request.setAttribute("mensagemErro", "Erro ao atualizar perfil. Tente novamente.");
            request.getRequestDispatcher("/gestor/perfil.jsp").forward(request, response);
        }
    }
}
