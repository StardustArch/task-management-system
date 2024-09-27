package com.taskmanagementee.controller.servlet;


import com.taskmanagementee.model.DAO.*;
import com.taskmanagementee.model.Tarefa;
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
import java.util.List;

@WebServlet("/gestor/tarefas")
public class TarefaListServlet extends HttpServlet {

    private TarefaDAO tarefaDAO;
    private ProjectoDAO projectoDAO;
    private MembroDAO membroDAO;
    private Usuario usuario;

    @Override
    public void init() throws ServletException {
        // Inicializar os serviços necessários
        // Inicializa a conexão e o DAO
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        tarefaDAO = new TarefaDAO(conn);
        projectoDAO = new ProjectoDAO(conn);
        membroDAO = new MembroDAO(conn);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtém a sessão do usuário
        HttpSession session = request.getSession();
        if (session.getAttribute("usuario") == null) {
            response.sendRedirect("/index.jsp");
            return;
        }

        usuario = (Usuario) session.getAttribute("usuario");

        // Obter a lista de tarefas
        List<Tarefa> tarefas = tarefaDAO.getAllTarefasPorGestor(usuario.getId());

        // Adicionar listas ao request
        request.setAttribute("tarefas", tarefas);
        request.setAttribute("projetos", getProjetos()); // Carregar projetos do banco
        request.setAttribute("responsaveis", getResponsaveis()); // Carregar projetos do banco


        // Redirecionar para a página JSP
        request.getRequestDispatcher("listarTarefas.jsp").forward(request, response);
    }

    public List<String> getProjetos() {

        return projectoDAO.buscarNomesProjetosPorGestor(usuario.getId());
    }
    public List<String> getResponsaveis(){
        return membroDAO.buscarNomesMembros();
    }
}

