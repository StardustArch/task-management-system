package com.taskmanagementee.controller.servlet;

import com.taskmanagementee.model.Tarefa;
import com.taskmanagementee.model.DAO.TarefaDAO;
import com.taskmanagementee.model.DAO.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/gestor/adicionarTarefa")
public class AdicionarTarefaServlet extends HttpServlet {

    private TarefaDAO tarefaDAO;
    private Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            connection = DatabaseConnection.getConnection();
            tarefaDAO = new TarefaDAO(connection);
        } catch (SQLException e) {
            throw new ServletException("Não foi possível inicializar o DAO", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("usuario") == null) {
            response.sendRedirect("/index.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        String descricao = request.getParameter("descricao");
        String status = request.getParameter("status");
        String dueDate = request.getParameter("dueDate");
        String responsavel = request.getParameter("responsavel");
        String projeto = request.getParameter("projeto");

        try {
            if (idStr == null || idStr.isEmpty()) {
                // Adicionar nova tarefa
                int numTarefasUsuario = tarefaDAO.contarTarefasPorUsuario(responsavel);

                if (numTarefasUsuario >= 5) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"message\": \"O usuário já possui 5 tarefas atribuídas. Não pode ser delegada mais nenhuma tarefa.\"}");
                    return;
                }

                tarefaDAO.addTarefa(descricao, status, java.sql.Date.valueOf(dueDate).toLocalDate(), projeto, responsavel);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"message\": \"Tarefa adicionada com sucesso!\"}");
            } else {
                // Atualizar tarefa existente
                int tarefaId = Integer.parseInt(idStr);
                Tarefa tarefaExistente = tarefaDAO.getTarefaById(tarefaId);

                if (tarefaExistente != null) {
                    // Obter o responsável antigo
                    String responsavelAntigo = tarefaExistente.getResponsavel();

                    // Verificar se o responsável mudou
                    boolean responsavelMudou = !responsavelAntigo.equals(responsavel);

                    if (responsavelMudou) {
                        // Verificar tarefas do novo responsável
                        int numTarefasNovoResponsavel = tarefaDAO.contarTarefasPorUsuario(responsavel);

                        if (numTarefasNovoResponsavel >= 3) {
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            response.getWriter().write("{\"message\": \"O novo usuário responsável já possui 3 tarefas atribuídas. Não pode ser delegada mais nenhuma tarefa.\"}");
                            return;
                        }
                    }

                    // Atualizar a tarefa
                    tarefaExistente.setDescricao(descricao);
                    tarefaExistente.setStatus(status);
                    tarefaExistente.setPrazo(java.sql.Date.valueOf(dueDate).toLocalDate());
                    tarefaExistente.setResponsavel(responsavel);
                    tarefaExistente.setProjeto(projeto);

                    tarefaDAO.updateTarefa(tarefaExistente.getId(), tarefaExistente.getDescricao(), tarefaExistente.getStatus(), tarefaExistente.getPrazo(), tarefaExistente.getProjeto(), tarefaExistente.getResponsavel());

                    if (responsavelMudou) {
                        // Verificar se o antigo responsável tem mais de 3 tarefas após a atualização
                        int numTarefasAntigoResponsavel = tarefaDAO.contarTarefasPorUsuario(responsavelAntigo);

                        if (numTarefasAntigoResponsavel > 3) {
                            // Aqui você pode optar por notificar o usuário ou tomar outras ações
                        }
                    }

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"message\": \"Tarefa atualizada com sucesso!\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"message\": \"Tarefa não encontrada.\"}");
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\": \"Erro ao salvar tarefa.\"}");
        }
    }

    @Override
    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

