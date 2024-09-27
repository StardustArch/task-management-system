<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.taskmanagementee.model.Projecto" %>
<%@ page import="java.util.List" %>

<html lang="pt">
<head>
    <meta charset="UTF-8">
    <title>Lista de Projectos</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        h1 {
            color: #333;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        table, th, td {
            border: 1px solid #ddd;
        }
        th, td {
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
        .action-buttons{
        width:30%;
        align-items:center;
        }
        .action-buttons a {
            margin-right: 10px;
            text-decoration: none;
            padding: 5px 10px;
            background-color: #007bff;
            color: #fff;
            border-radius: 5px;
        }
        button.delete-btn{
        margin-right: 10px;
                    text-decoration: none;
                    padding: 5px 10px;
                    background-color: #007bff;
                    color: #fff;
                    border-radius: 5px;
        }
        button.delete-btn:hover{
                    background-color: #0056b3;
        }
        .action-buttons a:hover {
            background-color: #0056b3;
        }
        .add-project-button {
            display: inline-block;
            margin-bottom: 20px;
            padding: 10px 20px;
            background-color: #28a745;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        .add-project-button:hover {
            background-color: #218838;
        }

    </style>
</head>
<body>

<div class="container">
    <h1>Lista de Projectos</h1>
    <a href="dashboard" class="dashboard-button">Voltar ao Dashboard</a>

    <!-- Botão para abrir o modal de Adicionar Projeto -->
    <div class="add-project-button" id="openModalBtn">Adicionar Projecto</div>
    <a class="add-project-button" href="exportarRelatorioConsolidadoPdf">Gerar Relatório de Projectos</a>

<%@ include file="modalAddProject.jsp" %>


    <!-- Tabela de Projetos -->
    <table>
        <thead>
            <tr>
                <!-- O ID não é exibido aqui -->
                <th>Nome</th>
                <th>Descrição</th>
                <th>Ações</th>
            </tr>
        </thead>
        <tbody>
            <%
                // Obtém a lista de projetos a partir da requisição
                List<Projecto> projectos = (List<Projecto>) request.getAttribute("projectos");
                if (projectos != null && !projectos.isEmpty()) {
                    for (Projecto projecto : projectos) {
            %>
            <tr>
                <!-- Ocultar o ID usando display:none -->
                <td style="display:none;"><%= projecto.getId() %></td>
                <td><%= projecto.getNome() %></td>
                <td><%= projecto.getDescricao() %></td>

                <td class="action-buttons">
                    <!-- ID ainda disponível nas ações -->
                    <a href="#" class="editProjectBtn"
                       data-id="<%= projecto.getId() %>"
                       data-nome="<%= projecto.getNome() %>"
                       data-descricao="<%= projecto.getDescricao() %>"
                       data-prazo="<%= projecto.getPrazo() %>">Editar</a>
                    <a href="excluirProjecto?id=<%= projecto.getId() %>" class="delete-btn" onclick="return confirm('Tem certeza de que deseja excluir este projeto?')">Excluir</a>
                    <a href="exportarRelatorioPdf?id=<%= projecto.getId() %>">Gerar relatório</a>
                </td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="3">Nenhum projeto encontrado.</td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
</div

</body>
</html>
