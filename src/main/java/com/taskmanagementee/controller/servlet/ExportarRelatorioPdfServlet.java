package com.taskmanagementee.controller.servlet;

import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.Projecto;
import com.taskmanagementee.model.Tarefa;
import com.taskmanagementee.model.DAO.ProjectoDAO;
import com.taskmanagementee.model.DAO.TarefaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.ServletOutputStream;
import com.lowagie.text.PageSize;
import org.xhtmlrenderer.pdf.ITextRenderer;
import com.lowagie.text.Rectangle;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/gestor/exportarRelatorioPdf")
public class ExportarRelatorioPdfServlet extends HttpServlet {
    private ProjectoDAO projectoDAO;
    private TarefaDAO tarefaDAO;
    private Connection conn;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            conn = DatabaseConnection.getConnection();
            tarefaDAO = new TarefaDAO(conn);
            projectoDAO = new ProjectoDAO(conn);
        } catch (SQLException e) {
            throw new ServletException("Erro ao conectar ao banco de dados durante init", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String projectIdParam = request.getParameter("id");

        if (projectIdParam != null) {
            try {
                int projectId = Integer.parseInt(projectIdParam);
                Projecto projecto = projectoDAO.buscarPorId2(projectId);
                List<Tarefa> tarefas = tarefaDAO.getTarefasByProjetoId(projectId);

                // Calcula a porcentagem de conclusão
                int totalTarefas = tarefas.size();
                int tarefasConcluidas = (int) tarefas.stream().filter(t -> "Concluida".equals(t.getStatus())).count();
                int tarefasPendentes = (int) tarefas.stream().filter(t -> "Pendente".equals(t.getStatus())).count();
                int tarefasAtrasadas = (int) tarefas.stream().filter(t -> "Atrasada".equals(t.getStatus())).count();
                int tarefasAndamento = totalTarefas - tarefasConcluidas - tarefasPendentes - tarefasAtrasadas;

                int porcentagemConclusao = totalTarefas > 0 ? (tarefasConcluidas * 100) / totalTarefas : 0;

                String dataCriacao = projecto.getData_criacao().toString();
                String ultimaAtualizacao = projectoDAO.getUltimaAtualizacao(projectId).toString();

                // Atribui os mesmos atributos usados na exibição da página ao request
                request.setAttribute("projecto", projecto);
                request.setAttribute("tarefas", tarefas);
                request.setAttribute("porcentagemConclusao", porcentagemConclusao);
                request.setAttribute("dataCriacao", dataCriacao);
                request.setAttribute("ultimaAtualizacao", ultimaAtualizacao);
                request.setAttribute("tarefasPendentes", tarefasPendentes);
                request.setAttribute("tarefasAndamento", tarefasAndamento);
                request.setAttribute("tarefasConcluidas", tarefasConcluidas);
                request.setAttribute("tarefasAtrasadas", tarefasAtrasadas);
                request.setAttribute("totalTarefas", totalTarefas);

                // Renderiza a página JSP para uma string HTML usando StringWriter
                String htmlContent = renderJspToHtml(request, response, "detalhesProjecto.jsp");
                Rectangle pageSizeA3 = new Rectangle(842, 1191);
                // Usa Flying Saucer para converter HTML em PDF
                try {
                    System.out.println("Rendered HTML: " + htmlContent);
                    ITextRenderer renderer = new ITextRenderer();
                    renderer.setDocumentFromString(htmlContent);

                    renderer.layout();

                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "attachment; filename=relatorio.pdf");

                    try (OutputStream outputStream = response.getOutputStream()) {
                        renderer.createPDF(outputStream);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ServletException("Erro ao gerar PDF", e);
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do projeto inválido.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do projeto não fornecido.");
        }
    }

    private String renderJspToHtml(HttpServletRequest request, HttpServletResponse response, String jspUrl) throws ServletException, IOException {
        // Cria um StringWriter para capturar o conteúdo gerado pela JSP
        StringWriter stringWriter = new StringWriter();
        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response) {
            @Override
            public PrintWriter getWriter() {
                return new PrintWriter(stringWriter);
            }

            @Override
            public ServletOutputStream getOutputStream() throws IOException {
                throw new IllegalStateException("getOutputStream() já foi chamado para esta resposta.");
            }
        };

        // Inclui a JSP e captura a saída no StringWriter
        request.getRequestDispatcher(jspUrl).include(request, responseWrapper);

        return stringWriter.toString();
    }

}
