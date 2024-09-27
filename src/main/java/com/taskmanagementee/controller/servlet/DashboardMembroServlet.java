package com.taskmanagementee.controller.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.taskmanagementee.model.Tarefa;
import com.taskmanagementee.model.Projecto;
import com.taskmanagementee.model.DAO.TarefaDAO;
import com.taskmanagementee.model.DAO.ProjectoDAO;
import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.TarefaProjetoDetalhes;
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

@WebServlet("/membro/dashboard")
public class DashboardMembroServlet extends HttpServlet {

    private TarefaDAO tarefaDAO;
    private ProjectoDAO projetoDAO;
    private Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            connection = DatabaseConnection.getConnection();
            tarefaDAO = new TarefaDAO(connection);
            projetoDAO = new ProjectoDAO(connection);
        } catch (Exception e) {
            throw new ServletException("Erro ao inicializar DAOs", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null || !"Membro".equals(usuario.getPapel())) {
            response.sendRedirect("/login.jsp");
            return;
        }

        // Ações de marcar status da tarefa
        String action = request.getParameter("action");
        String tarefaIdStr = request.getParameter("id");

        if (action != null && tarefaIdStr != null) {
            int tarefaId = Integer.parseInt(tarefaIdStr);

            if ("marcarAndamento".equals(action)) {
                marcarComoEmAndamento(tarefaId);
            } else if ("marcarConcluida".equals(action)) {
                marcarComoConcluida(tarefaId);
            }

            // Redireciona para o dashboard após marcar status
            response.sendRedirect("dashboard");
            return;
        }

        String nome = usuario.getNome();
        request.setAttribute("usuarioNome", nome);
        // Verifica se o ID do projeto foi passado corretamente



        try {
            // Buscar as tarefas do membro
            List<Tarefa> tarefas = tarefaDAO.getTarefasPorResponsavel(usuario.getId());
            request.setAttribute("tarefas", tarefas);

            // Contagem de status de tarefas
            long pendentes = tarefas.stream().filter(t -> t.getStatus().equals("Pendente")).count();
            long andamento = tarefas.stream().filter(t -> t.getStatus().equals("Em Andamento")).count();
            long concluidas = tarefas.stream().filter(t -> t.getStatus().equals("Concluida")).count();
            // Obter tarefas atrasadas
            List<Tarefa> tarefasAtrasadas = tarefaDAO.getTarefasAtrasadasPorResponsavel(usuario.getId());
            request.setAttribute("tarefasAtrasadas", tarefasAtrasadas);
            long atrasadas = tarefasAtrasadas.size();

            request.setAttribute("tarefasPendentes", pendentes);
            request.setAttribute("tarefasAndamento", andamento);
            request.setAttribute("tarefasConcluidas", concluidas);
            request.setAttribute("tarefasAtrasadas", atrasadas);
            request.setAttribute("totalTarefas", tarefas.size());


            // Buscar projetos do membro
            List<Projecto> projetos = projetoDAO.getProjetosPorMembro(usuario.getId());
            int projectosAtivos = projetoDAO.contarProjetosAtivosPorMembro(usuario.getId());
            request.setAttribute("projetos", projetos);
            request.setAttribute("projetosAtivos", projectosAtivos);
            String projectId = request.getParameter("projectId");
            if (projectId != null) {
                try {
                    int projectIdInt = Integer.parseInt(projectId);

                    // Buscar o projeto selecionado
                    Projecto projetoSelecionado = projetoDAO.buscarPorId2(projectIdInt);

                    // Buscar as tarefas associadas
                    List<TarefaProjetoDetalhes> tarefasAtribuidas = projetoDAO.getTarefasAtribuidas(usuario.getId(), projectIdInt);
//                    System.out.println(tarefasAtribuidas.get(0).getTarefaPrazo().toString());
                    // Criar um objeto JSON com os detalhes do projeto e tarefas
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("descricao", projetoSelecionado.getDescricao());
                    jsonResponse.addProperty("gestorNome", projetoSelecionado.getGestorNome());

                    JsonArray tarefasArray = new JsonArray();
                    for (TarefaProjetoDetalhes tarefa : tarefasAtribuidas) {
                        JsonObject tarefaJson = new JsonObject();
                        tarefaJson.addProperty("descricao", tarefa.getTarefaDescricao());
                        tarefaJson.addProperty("status", tarefa.getStatus());
                        tarefaJson.addProperty("prazo", tarefa.getTarefaPrazo() != null ? tarefa.getTarefaPrazo().toString() : null);
                        tarefaJson.addProperty("dataConclusao", tarefa.getDataConclusao() != null ? tarefa.getDataConclusao().toString() : null);
                        tarefasArray.add(tarefaJson);

                    }

                    jsonResponse.add("tarefas", tarefasArray);

                    // Configurar a resposta para JSON
                    response.setContentType("application/json");
                    response.getWriter().write(new Gson().toJson(jsonResponse));
                    return;

                } catch (NumberFormatException | SQLException e) {
                    e.printStackTrace();
                    throw new ServletException("Erro ao carregar detalhes do projeto", e);
                }
            }

            request.getRequestDispatcher("/membro/membroDashboard.jsp").forward(request, response);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para marcar tarefa como em andamento
    private void marcarComoEmAndamento(int tarefaId) throws ServletException {
        try {
            tarefaDAO.atualizarStatusTarefa(tarefaId, "Em Andamento");
        } catch (Exception e) {
            throw new ServletException("Erro ao marcar tarefa como Em Andamento", e);
        }
    }

    // Método para marcar tarefa como concluída
    private void marcarComoConcluida(int tarefaId) throws ServletException {
        try {
            tarefaDAO.atualizarStatusTarefa(tarefaId, "Concluida");
        } catch (Exception e) {
            throw new ServletException("Erro ao marcar tarefa como Concluída", e);
        }
    }

    @Override
    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}