package com.taskmanagementee.controller.service;

import com.taskmanagementee.model.Projecto;
import com.taskmanagementee.model.Tarefa;

import java.util.List;

public class RelatorioService {

    public String gerarRelatorioIndividual(Projecto projecto, List<Tarefa> tarefas, int porcentagemConclusao, String dataCriacao, String ultimaAtualizacao, int tarefasPendentes, int tarefasAndamento, int tarefasConcluidas, int tarefasAtrasadas, int totalTarefas) {
        StringBuilder html = new StringBuilder();

        html.append("<div style='font-family: Arial, sans-serif; background-color: #f8f9fa; margin: 0; padding: 20px;'>")
                .append("<div style='border: 1px solid #007bff; padding: 15px; margin-bottom: 20px; background-color: white;'>")
                .append("<h3 style='color: #007bff; margin-top: 0;'>Projecto: ").append(projecto.getNome()).append("</h3>")
                .append("<p>Data de criação: ").append(dataCriacao).append("</p>")
                .append("<p>Última atualização: ").append(ultimaAtualizacao).append("</p>")
                .append("<p>Conclusão: ").append(porcentagemConclusao).append("%</p>");

        html.append("<table style='width: 100%; border-collapse: collapse; margin-top: 20px;'>")
                .append("<thead>")
                .append("<tr>")
                .append("<th style='border: 1px solid black; padding: 8px; background-color: #007bff; color: white;'>Descrição</th>")
                .append("<th style='border: 1px solid black; padding: 8px; background-color: #007bff; color: white;'>Status</th>")
                .append("<th style='border: 1px solid black; padding: 8px; background-color: #007bff; color: white;'>Responsável</th>")
                .append("<th style='border: 1px solid black; padding: 8px; background-color: #007bff; color: white;'>Prazo</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

        // Verifica se há tarefas e as exibe
        if (tarefas != null && !tarefas.isEmpty()) {
            for (Tarefa tarefa : tarefas) {
                html.append("<tr>")
                        .append("<td style='border: 1px solid black; padding: 8px;'><strong>").append(tarefa.getDescricao()).append("</strong></td>")
                        .append("<td style='border: 1px solid black; padding: 8px;'>").append(tarefa.getStatus());
                if ("Concluida".equals(tarefa.getStatus()) && tarefa.getDataConclusao() != null) {
                    html.append("<br/><small>(Concluída em: ").append(tarefa.getDataConclusao().toString()).append(")</small>");
                }
                html.append("</td>")
                        .append("<td style='border: 1px solid black; padding: 8px;'>").append(tarefa.getResponsavelNome()).append("</td>")
                        .append("<td style='border: 1px solid black; padding: 8px;'>").append(tarefa.getPrazo() != null ? tarefa.getPrazo().toString() : "Sem prazo").append("</td>")
                        .append("</tr>");
            }
        } else {
            // Se não houver tarefas
            html.append("<tr>")
                    .append("<td colspan='4' style='border: 1px solid black; padding: 8px; text-align: center;'>Nenhuma tarefa encontrada para este projeto.</td>")
                    .append("</tr>");
        }

        html.append("</tbody>")
                .append("</table>")
                .append("</div>")
                .append("</div>");

        return html.toString();
    }
}
