        <%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <%@ page import="java.util.List" %>
        <%@ page import="com.taskmanagementee.model.Usuario" %>

        <html lang="pt">
        <head>
            <meta charset="UTF-8">
            <title>Lista de Membros</title>
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
                .action-buttons .add-member-btn {
                                          background-color: #28a745;
                                      }
                                      .action-buttons .add-member-btn:hover {
                                          background-color: #218838;
                                      }
                  .action-buttons .exit-btn {
                      background-color: #dc3545;
                  }
                  .action-buttons .exit-btn:hover {
                      background-color: #c82333;
                  }

            </style>

        </head>
        <body>
            <div class="container">
                <h1>Lista de Membros</h1>

                <!-- Botões de ação -->
                        <div class="action-buttons">
                            <a href="dashboard" class="exit-btn">Sair</a>
                            <button class="add-member-btn" id="openMembroModalBtn">Adicionar Membro</button>
                        </div>

                <!-- Tabela de membros -->
                <table>
                    <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Email</th>
                            <th>Função</th>
                            <th>Tarefas Atribuídas</th>
                            <th>Status</th>
                            <th>Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            // Obtém a lista de membros a partir da requisição
                            List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
                            if (usuarios != null && !usuarios.isEmpty()) {
                                for (Usuario usuario : usuarios) {
                        %>
                        <tr>
                            <td><%= usuario.getNome() %></td>
                            <td><%= usuario.getEmail() %></td>
                            <td><%= usuario.getFuncao_membro() %></td>
                            <td><%= usuario.getTarefasAtribuidas() %></td>
                            <td><%= usuario.getStatus() %></td>
                            <td>
                                <a href="#" class="editMemberBtn"
                                   data-id="<%= usuario.getId() %>"
                                   data-nome="<%= usuario.getNome() %>"
                                   data-email="<%= usuario.getEmail() %>"
                                   data-funcao="<%= usuario.getFuncao_membro() %>"
                                   data-tarefas="<%= usuario.getTarefasAtribuidas() %>"
                                   data-status="<%= usuario.getStatus() %>">Editar</a> |
                                <a href="excluirMembro?id=<%= usuario.getId() %>" onclick="return confirm('Tem certeza de que deseja excluir este membro?')">Excluir</a>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="6">Nenhum membro encontrado.</td>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            </div>

           <%@ include file="modalAddMember.jsp" %>

        </body>
        </html>
