package com.taskmanagementee.controller.servlet;

import com.lowagie.text.DocumentException;
import com.taskmanagementee.controller.service.RelatorioService;
import com.taskmanagementee.controller.service.ResumoAgregado;
import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.DAO.ProjectoDAO;
import com.taskmanagementee.model.DAO.TarefaDAO;
import com.taskmanagementee.model.Projecto;
import com.taskmanagementee.model.Tarefa;
import com.taskmanagementee.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/gestor/exportarRelatorioConsolidadoPdf")
public class ExportarRelatorioConsolidadoPdfServlet extends HttpServlet {
    private ProjectoDAO projectoDAO;
    private TarefaDAO tarefaDAO;
    private Connection conn;
    private RelatorioService relatorioService;
    private Usuario usuario;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            conn = DatabaseConnection.getConnection();
            tarefaDAO = new TarefaDAO(conn);
            projectoDAO = new ProjectoDAO(conn);
            relatorioService = new RelatorioService();
        } catch (SQLException e) {
            throw new ServletException("Erro ao conectar ao banco de dados", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            usuario = (Usuario) session.getAttribute("usuario");
            // Busca todos os projetos
            List<Projecto> listaDeProjectos = projectoDAO.buscarProjetosPorGestor(usuario.getId());

            // StringBuilder para armazenar o HTML consolidado
            StringBuilder relatorioConsolidadoHtml = new StringBuilder();
            relatorioConsolidadoHtml.append("<html><body><h1>Relatório Consolidado de Projetos</h1>");
            relatorioConsolidadoHtml.append("<p>Data de Geração: ").append(LocalDate.now()).append("</p>");
            relatorioConsolidadoHtml.append("<p>Autor: ").append(usuario.getNome()).append("</p>");

            // Resumo agregado
            ResumoAgregado resumo = gerarResumo(listaDeProjectos);

            // Exibir resumo agregado numa tabela
            relatorioConsolidadoHtml.append("<h2>Resumo Agregado</h2>");
            relatorioConsolidadoHtml.append("<table style='width: 70%; border-collapse: collapse; margin-top: 20px;'>");
            relatorioConsolidadoHtml.append("<tr><th style='border: 1px solid black; padding: 3px; background-color: #007bff; color: white;'>Métrica</th><th style='border: 1px solid black; padding: 3px; background-color: #007bff; color: white;'>Valor</th></tr>");
            relatorioConsolidadoHtml.append("<tr><td style='border: 1px solid black; padding: 3px;'>Total de Projectos</td><td style='border: 1px solid black; padding: 3px;'>").append(resumo.getTotalProjetos()).append("</td></tr>");
            relatorioConsolidadoHtml.append("<tr><td style='border: 1px solid black; padding: 3px;'>Taxa Média de Conclusão</td><td style='border: 1px solid black; padding: 3px;'>").append(String.format("%.1f", resumo.getTaxaConclusaoMedia())).append("%</td></tr>");
            relatorioConsolidadoHtml.append("<tr><td style='border: 1px solid black; padding: 3px;'>Total de Tarefas</td><td style='border: 1px solid black; padding: 3px;'>").append(resumo.getTotalTarefas()).append("</td></tr>");
            relatorioConsolidadoHtml.append("<tr><td style='border: 1px solid black; padding: 3px;'>Tarefas Concluídas</td><td style='border: 1px solid black; padding: 3px;'>").append(resumo.getTotalConcluidas()).append("</td></tr>");
            relatorioConsolidadoHtml.append("<tr><td style='border: 1px solid black; padding: 3px;'>Tarefas Pendentes</td><td style='border: 1px solid black; padding: 3px;'>").append(resumo.getTotalPendentes()).append("</td></tr>");
            relatorioConsolidadoHtml.append("<tr><td style='border: 1px solid black; padding: 3px;'>Tarefas Atrasadas</td><td style='border: 1px solid black; padding: 3px;'>").append(resumo.getTotalAtrasadas()).append("</td></tr>");
            relatorioConsolidadoHtml.append("<tr><td style='border: 1px solid black; padding: 3px;'>Tarefas em Andamento</td><td style='border: 1px solid black; padding: 3px;'>").append(resumo.getTotalAndamento()).append("</td></tr>");
            relatorioConsolidadoHtml.append("<tr><td style='border: 1px solid black; padding: 3px;'>Percentual de Projectos Concluídos</td><td style='border: 1px solid black; padding: 3px;'>").append(resumo.getPorcentagemProjetosConcluidos()).append("%</td></tr>");
            relatorioConsolidadoHtml.append("</table>");


            // Gera relatórios individuais para cada projeto e adiciona ao HTML consolidado
            for (Projecto projecto : listaDeProjectos) {
                List<Tarefa> tarefas = tarefaDAO.getTarefasByProjetoId(projecto.getId());
                int totalTarefas = tarefas.size();
                int tarefasConcluidas = (int) tarefas.stream().filter(t -> "Concluida".equals(t.getStatus())).count();
                int tarefasPendentes = (int) tarefas.stream().filter(t -> "Pendente".equals(t.getStatus())).count();
                int tarefasAtrasadas = (int) tarefas.stream().filter(t -> "Atrasada".equals(t.getStatus())).count();
                int tarefasAndamento = totalTarefas - tarefasConcluidas - tarefasPendentes - tarefasAtrasadas;
                int porcentagemConclusao = totalTarefas > 0 ? (tarefasConcluidas * 100) / totalTarefas : 0;
                String dataCriacao = projecto.getData_criacao().toString();
                String ultimaAtualizacao = projectoDAO.getUltimaAtualizacao(projecto.getId()).toString();

                // Gera o relatório individual usando o serviço
                String relatorioIndividualHtml = relatorioService.gerarRelatorioIndividual(
                        projecto, tarefas, porcentagemConclusao, dataCriacao, ultimaAtualizacao,
                        tarefasPendentes, tarefasAndamento, tarefasConcluidas, tarefasAtrasadas, totalTarefas
                );

                relatorioConsolidadoHtml.append(relatorioIndividualHtml);
            }

            // Fechar o HTML
            relatorioConsolidadoHtml.append("</body></html>");

            // Usa Flying Saucer para converter o HTML em PDF
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(relatorioConsolidadoHtml.toString());
            renderer.layout();

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=relatorio_consolidado.pdf");

            try (OutputStream outputStream = response.getOutputStream()) {
                renderer.createPDF(outputStream);
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ResumoAgregado gerarResumo(List<Projecto> projetos) {
        int totalProjetos = projetos.size();
        int totalTarefas = 0;
        int totalConcluidas = 0;
        int totalPendentes = 0;
        int totalAtrasadas = 0;
        int totalAndamento = 0;
        int projetosConcluidos = 0;  // Variável para contar projetos concluídos

        for (Projecto projeto : projetos) {
            List<Tarefa> tarefas = tarefaDAO.getTarefasByProjetoId(projeto.getId());
            totalTarefas += tarefas.size();
            totalConcluidas += (int) tarefas.stream().filter(t -> "Concluida".equals(t.getStatus())).count();
            totalPendentes += (int) tarefas.stream().filter(t -> "Pendente".equals(t.getStatus())).count();
            totalAtrasadas += (int) tarefas.stream().filter(t -> "Atrasada".equals(t.getStatus())).count();
            totalAndamento += (int) tarefas.stream().filter(t -> "Em Andamento".equals(t.getStatus())).count();

            // Verifica se o projeto está concluído, ou seja, se todas as suas tarefas estão concluídas
            boolean isProjetoConcluido = tarefas.stream().allMatch(t -> "Concluida".equals(t.getStatus()));
            if (isProjetoConcluido) {
                projetosConcluidos++;  // Incrementa o contador de projetos concluídos
            }
        }

        // Cálculo da taxa média de conclusão de tarefas (concluídas / total de tarefas)
        double taxaConclusaoMedia = totalTarefas > 0 ? (double) totalConcluidas / totalTarefas * 100 : 0;

        // Cálculo da porcentagem de projetos concluídos
        double porcentagemProjetosConcluidos = totalProjetos > 0 ? (double) projetosConcluidos / totalProjetos * 100 : 0;

        return new ResumoAgregado(totalProjetos, taxaConclusaoMedia, totalTarefas, totalConcluidas, totalPendentes, totalAtrasadas, totalAndamento, porcentagemProjetosConcluidos);
    }
}
