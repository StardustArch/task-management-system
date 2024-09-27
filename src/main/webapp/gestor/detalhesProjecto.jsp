<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="com.taskmanagementee.model.Projecto" %>
<%@ page import="com.taskmanagementee.model.Tarefa" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"></meta>
    <title>Relatório do Projeto</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css"></link>


    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f8f9fa;
        }

        /* Cabeçalho do relatório */
        .report-header {
            background-color: #007bff;
            color: white;
            padding: 15px;
            margin-bottom: 20px;
        }

        h2, h4 {
            color: white;
        }
        h5, h3 {
            color: #007bff;
        }

        /* Garantir que a tabela seja redimensionada adequadamente */
        table {
            width: 100%;
            table-layout: fixed; /* Garante que a tabela se ajuste ao layout da página */
            word-wrap: break-word; /* Quebra palavras longas para evitar que sejam cortadas */
            overflow: hidden; /* Evita que a tabela extrapole os limites da página */
            margin-top: 20px;
            border-collapse: collapse; /* Remove espaços entre células */
        }

        /* Garantir que células se ajustem e o conteúdo não ultrapasse os limites */
        table, th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: left;
            word-wrap: break-word; /* Quebra o texto nas células, se necessário */
        }



        /* Ajustar margens e tamanho da página ao gerar PDF */
        @page {
            size: A4;
            margin: 20mm; /* Ajuste conforme necessário */
            /* Previne que o conteúdo seja cortado nas margens da página */
        }

        /* Layout de cartões ajustado */
        .card {
            border: 1px solid #dee2e6;
            padding: 10px;
            margin: 10px 0;
            max-width: 250px;  /* Define um tamanho máximo para os cartões */
            flex: 1;  /* Para os cartões se ajustarem ao layout responsivo */
        }

        /* Centralizar os cartões */
        .cards-container {
            display: flex;
            justify-content: space-between;  /* Espaçamento entre os cartões */
            flex-wrap: wrap;  /* Permite quebra de linha quando necessário */
            gap: 15px;  /* Espaçamento entre os cartões */
        }

        /* Centralizar os títulos e o texto dentro dos cartões */
        .card-title {
            font-size: 16px;
            font-weight: bold;
            text-align: center;
        }

        .card-text {
            font-size: 14px;
            text-align: center;
        }

        .text-center {
            text-align: center;
        }

        /* Seção de progresso - ajuste dos cartões de progresso */
        .progress-summary {
            margin-top: 20px;
        }

        .spinner-container {
            text-align: center;
            margin: 10px 0;
        }

        .spinner-text {
            font-size: 18px;
            color: black;
        }


        /* Botões */
        .btn-custom {
            background-color: #007bff;
            color: white;
            text-decoration: none;
            padding: 10px 15px;
            border-radius: 5px;
            margin-top: 10px;
        }

        .btn-custom:hover {
            background-color: #0056b3;
        }

        /* Cartões de progresso específicos */
        .card.progress-card {
            max-width: 50px;
            margin: 10px auto; /* Centraliza os cartões no layout */
        }

        /* Cartões de tarefa ajustados para ficarem lado a lado */
        .cards-container.task-cards {
            display: block;
            flex-wrap: wrap;
            gap: 5px; /* Espaçamento entre os cartões */
            justify-content: flex-start; /* Garantir alinhamento correto na impressão */
        }

        .card.task-card {
            width: calc(33.33% - 10px); /* Ajuste para 3 cartões por linha */
            margin: 5px;
            flex-grow: 1;
        }

        @page {
            size: A3; /* Define o tamanho como A4 */
            margin: 0mm; /* Margens pequenas para evitar cortes */
            margin-top: 5mm;
        }

        @media print {


                   .no-print {
                        display: none !important;
                    }
          h5 {
                    color: #007bff;
                }


            .card {

                width: 200%;
               }
            .progress-card{
       width:45%;
       height:100%;
       display:inline-block;
       text-align:center;
            }
            .rows{
                            width:102.5%;
                            text-align:center;

                                            margin-bottom:100px;

            }


            body {
                margin: 0; /* Remove margens extras do body */
                padding: 0;
                                background-color: white; /* Garante um fundo branco */
                                  width: 1122px;
                                        height: 1191px;

            }
            h3.h3table{
            margin-top:100px;
            font-size:30px;
            }
            h3.h3tables{
                      margin-top:100px;
                        font-size:30px;
                        }

            table {
                width: 290px; /* Define a largura total da tabela */

                table-layout: fixed; /* Força as colunas a terem tamanho fixo */
            }

            tr {

            }

            th, td {

                width: 15px; /* Largura fixa das colunas */
                word-wrap: break-word; /* Quebra o conteúdo se for maior que a largura */
            }

            .cards-container {
                display: flex;
                flex-wrap: wrap;
                justify-content: space-between;
            }


        }




    </style>
</head>

<body>
    <!-- Seu conteúdo JSP aqui -->

<%
    Projecto projecto = (Projecto) request.getAttribute("projecto");
    List<Tarefa> tarefas = (List<Tarefa>) request.getAttribute("tarefas");
    Integer porcentagemConclusao = (Integer) request.getAttribute("porcentagemConclusao");
    String dataCriacao = (String) request.getAttribute("dataCriacao");
    String ultimaAtualizacao = (String) request.getAttribute("ultimaAtualizacao");
%>

<div class="container mt-4">
   <% if (projecto != null) { %>
       <div class="report-header">
           <div class="row">
               <!-- Coluna com as informações do projeto -->
               <div class="col-md-6">
                   <h2>Detalhes do Projeto: <%= projecto.getNome() %></h2>
                   <p><strong>Prazo:</strong> <%= projecto.getPrazo() %></p>
                   <p><strong>Data de Criação:</strong> <%= dataCriacao %></p>
                   <p><strong>Última Atualização:</strong> <%= ultimaAtualizacao %></p>
                   <p><strong>Gestor:</strong> <%= projecto.getGestorNome() %></p>
               </div>

               <!-- Coluna com a descrição do projeto -->
               <div class="col-md-6">
                   <h4><strong>Descrição do Projeto</strong></h4>
                   <p style="text-align: justify; word-wrap: break-word;">
                       <%= projecto.getDescricao() %>
                   </p>
               </div>
           </div>
       </div>



    <h3 class="h3table">Tarefas</h3>
    <table class="table table-striped">
        <thead>
            <tr>
                <th>Descrição</th>
                <th>Status</th>
                <th>Responsável</th>
                <th>Prazo</th>
            </tr>
        </thead>
        <tbody>
            <% if (tarefas != null && !tarefas.isEmpty()) {
                for (Tarefa tarefa : tarefas) { %>
                    <tr>
                        <td><strong><%= tarefa.getDescricao() %></strong></td>

                        <td>
                            <%= tarefa.getStatus() %>
                            <% if ("Concluida".equals(tarefa.getStatus()) && tarefa.getDataConclusao() != null) { %>
                                <br />
                                <small>(Concluída em: <%= tarefa.getDataConclusao().toString() %>)</small>
                            <% } %>
                        </td>

                        <td><%= tarefa.getResponsavelNome() %></td>

                        <td><%= tarefa.getPrazo() != null ? tarefa.getPrazo().toString() : "Sem prazo" %></td>
                    </tr>
            <% } } else { %>
                <tr>
                    <td colspan="4" class="text-center">Nenhuma tarefa encontrada para este projeto.</td>
                </tr>
            <% } %>
        </tbody>
    </table>


 <!-- Resumo Geral -->
 <h3 class="h3tables">Resumo do Progresso</h3>
 <div class="row rows">
     <%
         // Declaração das variáveis apenas uma vez
         Integer totalTarefas = (Integer) request.getAttribute("totalTarefas");
         Integer tarefasPendentes = (Integer) request.getAttribute("tarefasPendentes");
         Integer tarefasAndamento = (Integer) request.getAttribute("tarefasAndamento");
         Integer tarefasConcluidas = (Integer) request.getAttribute("tarefasConcluidas");
         Integer tarefasAtrasadas = (Integer) request.getAttribute("tarefasAtrasadas");

         // Cálculo das porcentagens
         double percentagemPendentes = (totalTarefas != null && totalTarefas > 0) ?
             ((double) tarefasPendentes / totalTarefas * 100) : 0;
         double percentagemAndamento = (totalTarefas != null && totalTarefas > 0) ?
             ((double) tarefasAndamento / totalTarefas * 100) : 0;
         double percentagemConcluidas = (totalTarefas != null && totalTarefas > 0) ?
             ((double) tarefasConcluidas / totalTarefas * 100) : 0;
         double percentagemAtrasadas = (totalTarefas != null && totalTarefas > 0) ?
             ((double) tarefasAtrasadas / totalTarefas * 100) : 0;

         String percentTextPendentes = String.format("%.1f", percentagemPendentes);
         String percentTextAndamento = String.format("%.1f", percentagemAndamento);
         String percentTextConcluidas = String.format("%.1f", percentagemConcluidas);
         String percentTextAtrasadas = String.format("%.1f", percentagemAtrasadas);
     %>

     <!-- Card para Tarefas Pendentes -->
     <div class="col-md-3 progress-card">
         <div class="card">
             <div class="card-body">
                 <h5 class="card-title">Tarefas Pendentes</h5>
                 <div class="spinner-container">
                     <div class="spinner" style="--primary: black; --value: <%= percentagemPendentes %>;">
                     </div>
                     <span class="spinner-text">
                         <%= percentTextPendentes %> %
                     </span>
                 </div>
                 <p class="card-text"><%= tarefasPendentes %> Tarefas</p>
             </div>
         </div>
     </div>

     <!-- Card para Tarefas em Andamento -->
     <div class="col-md-3 progress-card">
         <div class="card">
             <div class="card-body">
                 <h5 class="card-title">Tarefas em Andamento</h5>
                 <div class="spinner-container">
                     <div class="spinner" style="--primary: blue; --value: <%= percentagemAndamento %>;">
                     </div>
                     <span class="spinner-text">
                         <%= percentTextAndamento %> %
                     </span>
                 </div>
                 <p class="card-text"><%= tarefasAndamento %> Tarefas</p>
             </div>
         </div>
     </div>

     <!-- Card para Tarefas Concluídas -->
     <div class="col-md-3 progress-card">
         <div class="card">
             <div class="card-body">
                 <h5 class="card-title">Tarefas Concluídas</h5>
                 <div class="spinner-container">
                     <div class="spinner" style="--primary: green; --value: <%= percentagemConcluidas %>;">
                     </div>
                     <span class="spinner-text">
                         <%= percentTextConcluidas %> %
                     </span>
                 </div>
                 <p class="card-text"><%= tarefasConcluidas %> Tarefas</p>
             </div>
         </div>
     </div>

     <!-- Card para Tarefas Atrasadas -->
     <div class="col-md-3 progress-card">
         <div class="card">
             <div class="card-body">
                 <h5 class="card-title">Tarefas Atrasadas</h5>
                 <div class="spinner-container">
                     <div class="spinner" style="--primary: red; --value: <%= percentagemAtrasadas %>;">
                     </div>
                     <span class="spinner-text">
                         <%= percentTextAtrasadas %> %
                     </span>
                 </div>
                 <p class="card-text"><%= tarefasAtrasadas %> Tarefas</p>
             </div>
         </div>
     </div>
 </div>

 <!-- Seção de Resumo do Progresso -->
 <div class="progress-summary mt-4">
     <h3>Percentagem de Conclusão</h3>
     <div class="row progress-card">
         <!-- Coluna para exibir a taxa de conclusão -->
         <div class="col-md-3">
             <div class="card text-center">
                 <div class="card-body">
                     <h5 class="card-title">Conclusão do Projeto</h5>
                     <div class="spinner-container">
                         <div class="spinner" style="--primary: cyan; --value: <%= porcentagemConclusao %>;">
                         </div>
                         <span class="spinner-text">
                             <%= porcentagemConclusao %> %
                         </span>
                     </div>
                 </div>
             </div>
         </div>
     </div>
 </div>
 <% } else { %>
     <div class="alert alert-danger">Projeto não encontrado.</div>
 <% } %>


  <div class="mt-4 d-flex justify-content-between no-print">
    <a href="projectos" class="btn btn-custom">Voltar para a Lista de Projetos</a>

    <% if (projecto != null) { %>
<a href="exportarRelatorioPdf?id=<%= projecto.getId() %>" class="btn btn-secondary">Exportar PDF</a>    <% } else { %>
        <span class="text-danger">Não é possível exportar PDF, o projeto não está disponível.</span>
    <% } %>
   </div>

</div>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.0.7/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

</body>
</html>
