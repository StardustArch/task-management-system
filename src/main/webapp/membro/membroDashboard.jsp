    <%@ page import="java.util.List" %>
    <%@ page import="com.taskmanagementee.model.Tarefa" %>
    <%@ page import="com.taskmanagementee.model.Projecto" %>
    <%@ page import="com.taskmanagementee.model.TarefaProjetoDetalhes" %>
    <%@ page import="com.taskmanagementee.model.DAO.ProjectoDAO" %>
    <%@ page import="com.taskmanagementee.model.DAO.DatabaseConnection" %>

    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="pt">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Dashboard do Membro</title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
        <style>
            .task-status-pendente { color: red; }
            .task-status-andamento { color: orange; }
            .task-status-concluida { color: green; }

            .card-body {
                display: flex;
                flex-direction: column;
                align-items: center; /* Centraliza horizontalmente */
                justify-content: center; /* Centraliza verticalmente */
                text-align: center; /* Centraliza o texto */
                height: 100%; /* Garante que o conteúdo preencha o card */
            }



    .spinner-container {
        position: relative;
        width: 100px; /* Ajuste o tamanho conforme necessário */
        height: 100px; /* Ajuste o tamanho conforme necessário */
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column; /* Permite que o texto fique embaixo do spinner */

    }

    .spinner {
        --value: 67; /* Valor padrão, será substituído dinamicamente */
        --percentage: 0; /* Inicialmente 0% */
        --primary: black; /* Cor do progresso */
        --secondary: transparent; /* Cor da parte não preenchida */
        --size: 100px; /* Ajuste o tamanho conforme necessário */
        width: var(--size);
        height: var(--size);
        border-radius: 50%;
        position: relative;
        overflow: hidden;
        display: grid;
        place-items: center;
        animation: progress-animation  3s ease-in-out forwards ; /* Animação do progresso */
    }

    .spinner::before {
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        border-radius: 50%;
        background: conic-gradient(
            var(--primary) 0% calc(var(--percentage) * 1%),
            var(--secondary) calc(var(--percentage) * 1%) 100%
        );
        mask: radial-gradient(circle, transparent 55%, black 56%);
        -webkit-mask: radial-gradient(circle, transparent 55%, black 56%);
    }

    .spinner-text {
        position: absolute;
        font-size: 1.2em;
        color: black; /* Ajuste a cor conforme necessário */
    }

    /* Animação do progresso */
    @keyframes progress-animation {
        from {
            --percentage: 0;
        }
        to {
            --percentage: var(--value);
        }
    }
        </style>
    </head>
    <body>
        <nav class="navbar navbar-expand-lg navbar-light bg-light">
            <a class="navbar-brand" href="#">Dashboard do Membro</a>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ml-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="../logout">Sair</a>
                    </li>
                </ul>
            </div>
        </nav>

    <div class="container mt-4">
        <h1>Bem-vindo, <%= request.getAttribute("usuarioNome") %></h1>

        <!-- Resumo Geral -->
        <div class="row">
            <!-- Tarefas Pendentes -->
            <div class="col-md-3">
                <div class="card">
                    <div class="card-body text-center">
                        <h5 class="card-title">Tarefas Pendentes</h5>
                        <div class="spinner-container">
                            <div class="spinner" style="
                                --primary: black;
                                --value: <%= ((Number)request.getAttribute("tarefasPendentes")).doubleValue() /
                                             ((Number)request.getAttribute("totalTarefas")).doubleValue() * 100 %>;">
                            </div>
                            <span class="spinner-text">
                                <%= String.format("%.1f", ((Number)request.getAttribute("tarefasPendentes")).doubleValue() /
                                     ((Number)request.getAttribute("totalTarefas")).doubleValue() * 100) %>%
                            </span>
                        </div>
                        <p class="card-text"><%= request.getAttribute("tarefasPendentes") %></p>
                    </div>
                </div>
            </div>

            <!-- Tarefas em Andamento -->
            <div class="col-md-3">
                <div class="card">
                    <div class="card-body text-center">
                        <h5 class="card-title">Tarefas em Andamento</h5>
                        <div class="spinner-container">
                            <div class="spinner" style="
                                --primary: blue;
                                --value: <%= ((Number)request.getAttribute("tarefasAndamento")).doubleValue() /
                                             ((Number)request.getAttribute("totalTarefas")).doubleValue() * 100 %>;">
                            </div>
                            <span class="spinner-text">
                                <%= String.format("%.1f", ((Number)request.getAttribute("tarefasAndamento")).doubleValue() /
                                     ((Number)request.getAttribute("totalTarefas")).doubleValue() * 100) %>%
                            </span>
                        </div>
                        <p class="card-text"><%= request.getAttribute("tarefasAndamento") %></p>
                    </div>
                </div>
            </div>

            <!-- Tarefas Concluídas -->
            <div class="col-md-3">
                <div class="card">
                    <div class="card-body text-center">
                        <h5 class="card-title">Tarefas Concluídas</h5>
                        <div class="spinner-container">
                            <div class="spinner" style="
                                --primary: green;
                                --value: <%= ((Number)request.getAttribute("tarefasConcluidas")).doubleValue() /
                                             ((Number)request.getAttribute("totalTarefas")).doubleValue() * 100 %>;">
                            </div>
                            <span class="spinner-text">
                                <%= String.format("%.1f", ((Number)request.getAttribute("tarefasConcluidas")).doubleValue() /
                                     ((Number)request.getAttribute("totalTarefas")).doubleValue() * 100) %>%
                            </span>
                        </div>
                        <p class="card-text"><%= request.getAttribute("tarefasConcluidas") %></p>
                    </div>
                </div>
            </div>

            <!-- Tarefas em Atraso -->
            <div class="col-md-3">
                <div class="card">
                    <div class="card-body text-center">
                        <h5 class="card-title">Tarefas em Atraso</h5>
                        <div class="spinner-container">
                            <div class="spinner" style="
                                --primary: red;
                                --value: <%= ((Number)request.getAttribute("tarefasAtrasadas")).doubleValue() /
                                             ((Number)request.getAttribute("totalTarefas")).doubleValue() * 100 %>;">
                            </div>
                            <span class="spinner-text">
                                <%= String.format("%.1f", ((Number)request.getAttribute("tarefasAtrasadas")).doubleValue() /
                                     ((Number)request.getAttribute("totalTarefas")).doubleValue() * 100) %>%
                            </span>
                        </div>
                        <p class="card-text"><%= request.getAttribute("tarefasAtrasadas") %></p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Projetos Ativos -->
        <div class="row mt-4">
            <div class="col-md-3">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Projetos Ativos</h5>
                        <p class="card-text"><%= request.getAttribute("projetosAtivos") %></p>
                    </div>
                </div>
            </div>
        </div>
    </div><br><br>

            <!-- Lista de Tarefas -->
            <div class="mt-4">
                <h3>Minhas Tarefas</h3>
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th>Descrição</th>
                            <th>Status</th>
                            <th>Prazo</th>
                            <th>Projeto</th>
                            <th>Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<Tarefa> tarefas = (List<Tarefa>) request.getAttribute("tarefas");
                            for (Tarefa tarefa : tarefas) {
                        %>
                            <tr>
                              <td><%= tarefa.getDescricao() %></td>
                                  <td class="task-status-<%= tarefa.getStatus().toLowerCase() %>">
                                      <%= tarefa.getStatus() %>
                                  </td>
                                  <td>
                                      <%= tarefa.getPrazo() %>
                                      <% if ("Concluida".equals(tarefa.getStatus())) { %>
                                          <br><small>(Concluída em: <%= tarefa.getDataConclusao() %>)</small>
                                      <% } %>
                                  </td>
                                  <td><%= tarefa.getProjeto() %></td>

                                <td>
    <!-- Botão para abrir o modal -->
                <button type="button" class="btn btn-info btn-sm"
                    data-toggle="modal"
                    data-target="#taskDetailModal"
                    data-descricao="<%= tarefa.getDescricao() %>"
                    data-status="<%= tarefa.getStatus() %>"
                    data-prazo="<%= tarefa.getPrazo() %>"
                    data-projeto="<%= tarefa.getProjeto() %>"
                    data-gestor-nome="<%= tarefa.getGestor() %>"
                    data-gestor-email="<%= tarefa.getGestorEmail() %>"
                    data-data-conclusao="<%= tarefa.getDataConclusao() %>">
                    Ver Detalhes
                </button>
                                    <!-- Verifica o status da tarefa e exibe o botão adequado -->
                                        <% if ("Pendente".equals(tarefa.getStatus())) { %>
                                            <a href="dashboard?action=marcarAndamento&id=<%= tarefa.getId() %>" class="btn btn-warning btn-sm">Marcar como Em Andamento</a>
                                        <% } else if ("Em Andamento".equals(tarefa.getStatus())) { %>
                                            <a href="dashboard?action=marcarConcluida&id=<%= tarefa.getId() %>" class="btn btn-success btn-sm">Concluir</a>
                                        <% } %>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>

      <!-- Lista de Projetos Envolvidos -->
      <div class="mt-4">
          <h3>Projetos Envolvidos</h3>
          <ul class="list-group">
              <%
                  List<Projecto> projetos = (List<Projecto>) request.getAttribute("projetos");
                  for (Projecto projeto : projetos) {
              %>
                  <li class="list-group-item d-flex justify-content-between align-items-center">
                      <strong><%= projeto.getNome() %></strong> - Gestor: <%= projeto.getGestorNome() %>

                      <button type="button" class="btn btn-info btn-sm"
                          onclick="carregarDetalhesProjeto(<%= projeto.getId() %>)">
                          Ver Detalhes
                      </button>
                  </li>
              <% } %>
          </ul>
      </div>

    <!-- Modal para Detalhes da Tarefa -->
    <div class="modal fade" id="taskDetailModal" tabindex="-1" aria-labelledby="taskDetailModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="taskDetailModalLabel">Detalhes da Tarefa</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Fechar">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <p><strong>Descrição:</strong> <span id="modalTaskDescription"></span></p>
                    <p><strong>Status:</strong> <span id="modalTaskStatus"></span></p>
                    <p><strong>Prazo:</strong> <span id="modalTaskDeadline"></span></p>
                    <p><strong>Projeto:</strong> <span id="modalTaskProject"></span></p>
                    <p><strong>Gestor Responsável:</strong> <span id="modalTaskGestor"></span>(<span id="modalTaskGestorEmail"></span>)</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Fechar</button>
                </div>
            </div>
        </div>
    </div>


  <div class="modal fade" id="projectDetailModal" tabindex="-1" role="dialog" aria-labelledby="projectDetailModalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
          <div class="modal-content">
              <div class="modal-header">
                  <h5 class="modal-title" id="projectDetailModalLabel">Detalhes do Projeto</h5>
                  <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                      <span aria-hidden="true">&times;</span>
                  </button>
              </div>
              <div class="modal-body">
                  <p id="project-description">Descrição:</p>
                  <p id="project-gestor">Gestor:</p>

                  <h5>Tarefas Atribuídas</h5>
                  <ul id="tarefas-list">
                      <!-- Tarefas inseridas dinamicamente aqui -->
                  </ul>
              </div>
          </div>
      </div>
  </div>

        <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.0.7/dist/umd/popper.min.js"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

    <script>
    function calcularDiasConclusao(prazo, dataConclusao) {
        var dataPrazo = new Date(prazo);
        var dataConclusaoDate = new Date(dataConclusao);

        var diferenca = dataConclusaoDate - dataPrazo;
        var diasDiferenca = Math.ceil(diferenca / (1000 * 60 * 60 * 24));

        if (diasDiferenca < 0) {
            return { texto: Math.abs(diasDiferenca) + " dias antes do prazo", cor: "black" };
        } else if (diasDiferenca === 0) {
            return { texto: "Concluída no prazo", cor: "black" };
        } else {
            return { texto: diasDiferenca + " dias depois do prazo", cor: "red" };
        }
    }

    function calcularDiasRestantes(prazo) {
        var dataPrazo = new Date(prazo);
        var dataAtual = new Date();

        var diferenca = dataPrazo - dataAtual;
        var diasRestantes = Math.ceil(diferenca / (1000 * 60 * 60 * 24));

        if (diasRestantes > 0) {
            return { texto: diasRestantes + " dias restantes", cor: "black" };
        } else if (diasRestantes === 0) {
            return { texto: "Hoje é o prazo", cor: "orange" };
        } else {
            return { texto: "Prazo expirado", cor: "red" };
        }
    }

    $('#taskDetailModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var descricao = button.data('descricao');
        var status = button.data('status');
        var prazo = button.data('prazo');
        var projeto = button.data('projeto');
        var gestorNome = button.data('gestor-nome');
        var gestorEmail = button.data('gestor-email');
        var dataConclusao = button.data('data-conclusao'); // Adiciona este data-attribute

        var modal = $(this);
        modal.find('#modalTaskDescription').text(descricao);
        modal.find('#modalTaskStatus').text(status);
        modal.find('#modalTaskProject').text(projeto);
        modal.find('#modalTaskGestor').text(gestorNome);
        modal.find('#modalTaskGestorEmail').text(gestorEmail);

        if (status === "Concluida") {
            // Se a tarefa estiver concluída, calcula a diferença em relação ao prazo
            var prazoInfo = calcularDiasConclusao(prazo, dataConclusao);
            modal.find('#modalTaskDeadline').text(prazo + " (" + prazoInfo.texto + ")").css('color', prazoInfo.cor);
        } else {
            // Se não estiver concluída, calcula os dias restantes
            var prazoInfo = calcularDiasRestantes(prazo);
            modal.find('#modalTaskDeadline').text(prazo + " (" + prazoInfo.texto + ")").css('color', prazoInfo.cor);
        }
    });

function carregarDetalhesProjeto(projectId) {
    // Faz uma requisição AJAX ao servlet
    fetch('<%= request.getContextPath() %>/membro/dashboard?projectId=' + projectId)
        .then(response => response.json())
        .then(data => {
            // Atualiza o conteúdo do modal com os dados do projeto
            document.getElementById('project-description').innerText = 'Descrição: ' + data.descricao;
            document.getElementById('project-gestor').innerText = 'Gestor: ' + data.gestorNome;

            let tarefasList = document.getElementById('tarefas-list');
            tarefasList.innerHTML = ''; // Limpa a lista de tarefas
            data.tarefas.forEach(tarefa => {
                let li = document.createElement('li');
                li.innerHTML =
                    '<strong>Descrição:</strong> ' + tarefa.descricao + '<br>' +
                    '<strong>Status:</strong> ' + tarefa.status + '<br>' +
                    '<strong>Prazo:</strong> ' + (tarefa.prazo ? tarefa.prazo : 'Sem prazo') + '<br>' +
                    '<strong>Data de Conclusão:</strong> ' + (tarefa.dataConclusao ? tarefa.dataConclusao : 'Ainda não concluída');
                tarefasList.appendChild(li);
            });

            // Exibe o modal
            $('#projectDetailModal').modal('show');
        })
        .catch(error => console.error('Erro ao carregar detalhes do projeto:', error));
}

    </script>

    </body>
    </html>
