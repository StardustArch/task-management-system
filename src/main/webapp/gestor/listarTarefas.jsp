<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taskmanagementee.model.Tarefa" %>
<%@ page import="com.taskmanagementee.model.Usuario" %>
<%@ page import="com.taskmanagementee.model.Projecto" %>


<html lang="pt">
<head>
    <meta charset="UTF-8">
    <title>Gerenciamento de Tarefas</title>
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
            text-align: center;
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
        .action-buttons {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
        }
        .action-buttons button, .action-buttons a {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            color: #fff;
            cursor: pointer;
            text-decoration: none;
            font-size: 16px;
        }
        .action-buttons .add-task-btn {
            background-color: #28a745;
        }
        .action-buttons .add-task-btn:hover {
            background-color: #218838;
        }
        .action-buttons .exit-btn {
            background-color: #dc3545;
        }
        .action-buttons .exit-btn:hover {
            background-color: #c82333;
        }

        /* Estilo do modal */
        .modal {
            display: none;
            position: fixed;
            z-index: 1;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            overflow: auto; /* Adiciona rolagem se o modal for muito alto */
        }
        .modal-content {
            background-color: #fff;
            margin: auto;
            padding: 20px;
            border: 1px solid #888;
            width: 50%;
            border-radius: 8px;
            position: relative;
            top: 50%;
            transform: translateY(-50%);
        }
        .close {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
        }
        .close:hover,
        .close:focus {
            color: black;
            text-decoration: none;
            cursor: pointer;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
        }
        .form-group input, .form-group textarea, .form-group select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        .form-group button {
            padding: 10px 20px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        .form-group button:hover {
            background-color: #218838;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Gerenciamento de Tarefas</h1>

        <!-- Botões de ação -->
        <div class="action-buttons">
            <a href="dashboard" class="exit-btn">Sair</a>
            <button class="add-task-btn" id="openTaskModalBtn">Adicionar Tarefa</button>
        </div>

       <!-- Tabela de tarefas -->
       <table>
           <thead>
               <tr>
                   <!-- Remover a exibição do ID no cabeçalho -->
                   <th>Descrição</th>
                   <th>Projeto</th>
                   <th>Responsável</th>
                   <th>Status</th>
                   <th>Prazo</th>
                   <th>Ações</th>
               </tr>
           </thead>
           <tbody>
               <%
                   List<Tarefa> tarefas = (List<Tarefa>) request.getAttribute("tarefas");
                   if (tarefas != null && !tarefas.isEmpty()) {
                       for (Tarefa tarefa : tarefas) {
               %>
               <tr>
                   <!-- Ocultar o ID usando display:none -->
                   <td style="display:none;"><%= tarefa.getId() %></td>
                   <td><%= tarefa.getDescricao() %></td>
                   <td><%= tarefa.getProjeto() %></td>
                   <td><%= tarefa.getResponsavel() %></td>
                   <td><%= tarefa.getStatus() %></td>
                   <td><%= tarefa.getPrazo() %></td>

                   <td>
                       <!-- ID ainda disponível para ações -->
                       <a href="#" class="editTaskBtn"
                          data-id="<%= tarefa.getId() %>"
                          data-descricao="<%= tarefa.getDescricao() %>"
                          data-projeto="<%= tarefa.getProjeto() != null ? tarefa.getProjeto() : "" %>"
                          data-responsavel="<%= tarefa.getResponsavel() %>"
                          data-dueDate="<%= tarefa.getPrazo() %>"
                          data-status="<%= tarefa.getStatus() %>">Editar</a> |
                       <a href="excluirTarefa?id=<%= tarefa.getId() %>" onclick="return confirm('Tem certeza de que deseja excluir esta tarefa?')">Excluir</a>
                   </td>
               </tr>
               <%
                       }
                   } else {
               %>
               <tr>
                   <td colspan="6">Nenhuma tarefa encontrada.</td>
               </tr>
               <%
                   }
               %>
           </tbody>
       </table>

    </div>

    <%@ include file="modalAddTarefa.jsp" %>

</body>
</html>
