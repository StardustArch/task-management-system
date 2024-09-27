        <%@ page import="com.taskmanagementee.model.Membro" %>
        <%@ page import="com.taskmanagementee.model.Projecto" %>
        <%@ page import="java.util.List" %>
        <%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <!DOCTYPE html>


            <style>
                /* Estilo do modal de tarefas */
                .task-modal {
                    display: none;
                    position: fixed;
                    z-index: 1;
                    left: 0;
                    top: 0;
                    width: 100%;
                    height: 100%;
                    background-color: rgba(0, 0, 0, 0.5);
                    overflow: auto;
                }

                .task-modal-content {
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

                .task-close {
                    color: #aaa;
                    float: right;
                    font-size: 28px;
                    font-weight: bold;
                    cursor: pointer;
                }

                .task-close:hover,
                .task-close:focus {
                    color: black;
                    text-decoration: none;
                    cursor: pointer;
                }

                .task-form-group {
                    margin-bottom: 15px;
                }

                .task-form-group label {
                    display: block;
                    margin-bottom: 5px;
                }

                .task-form-group input,
                .task-form-group textarea,
                .task-form-group select {
                    width: 100%;
                    padding: 8px;
                    border: 1px solid #ccc;
                    border-radius: 4px;
                }

                .task-form-group button {
                    padding: 10px 20px;
                    background-color: #28a745;
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                }

                .task-form-group button:hover {
                    background-color: #218838;
                }
            </style>



                <!-- Botões e outras partes do dashboard -->

                <!-- Modal para Adicionar ou Editar Tarefa -->
                <div id="taskModal" class="task-modal">
                    <div class="task-modal-content">
                        <span class="task-close" id="closeTaskModalBtn">&times;</span>
                        <h2 id="taskModalTitle">Adicionar Nova Tarefa</h2>
                        <form id="taskForm" method="post" action="adicionarTarefa">
                            <input type="hidden" id="taskId" name="id" value=""> <!-- Campo oculto para ID -->

                            <div class="task-form-group">
                                <label for="descricao">Descrição:</label>
                                <textarea id="descricaoTarefa" name="descricao" required></textarea>
                            </div>

                            <div class="task-form-group">
                                <label for="status">Status:</label>
                                <select id="status" name="status" required>
                                    <option value="Pendente">Pendente</option>
                                    <option value="Em Andamento">Em Andamento</option>
                                    <option value="Concluida">Concluida</option>
                                </select>
                            </div>

                            <div class="task-form-group">
                                <label for="dueDate">Prazo:</label>
                                <input type="date" id="dueDate" name="dueDate" required>
                            </div>

                            <div class="task-form-group">
                                <label for="responsavel">Responsável:</label>
                                <select id="responsavel" name="responsavel" required>
                                    <% List<String> membros = (List<String>) request.getAttribute("responsaveis");
                                       for (String membro : membros) { %>
                                        <option value="<%= membro %>"><%= membro %></option>
                                    <% } %>
                                </select>
                            </div>

                            <div class="task-form-group">
                                <label for="projeto">Projeto:</label>
                                <select id="projetoTask" name="projeto" required>
                                    <% List<String> projectos = (List<String>) request.getAttribute("projetos");
                                       for (String projeto : projectos) { %>
                                        <option value="<%= projeto %>"><%= projeto %></option>
                                    <% } %>
                                </select>
                            </div>

                            <div class="task-form-group">
                                <button type="submit" id="submitTaskBtn">Adicionar Tarefa</button>
                            </div>
                        </form>
                    </div>
                </div>


            <script>


document.addEventListener('DOMContentLoaded', function() {
                    var modal = document.getElementById("taskModal");
                    var openModalBtn = document.getElementById("openTaskModalBtn");
                    var closeModalBtn = document.getElementById("closeTaskModalBtn");
                    var taskId = document.getElementById("taskId");
                   var descricao = document.getElementById("descricaoTarefa");
                    var status = document.getElementById("status");
                    var dueDate = document.getElementById("dueDate");
                    var responsavel = document.getElementById("responsavel");
                    var projeto = document.getElementById("projetoTask");
                    var modalTitle = document.getElementById("taskModalTitle");
                    var submitBtn = document.getElementById("submitTaskBtn");

                    openModalBtn.onclick = function() {
                        modalTitle.textContent = "Adicionar Nova Tarefa";
                        submitBtn.textContent = "Adicionar Tarefa";
                        taskId.value = "";
                        descricao.value = "";
                        status.value = "Pendente";
                        dueDate.value = "";
                        responsavel.value = "";
                        projeto.value = "";
                        modal.style.display = "block";
                    }

                    closeModalBtn.onclick = function() {
                        modal.style.display = "none";
                    }

                    window.onclick = function(event) {
                        if (event.target == modal) {
                            modal.style.display = "none";
                        }
                    }

                    var editTaskBtns = document.querySelectorAll(".editTaskBtn");
                    editTaskBtns.forEach(function(btn) {
                        btn.onclick = function() {
                            var id = this.getAttribute("data-id");
                            var descricaoTarefa = this.getAttribute("data-descricao");
                            var statusTarefa = this.getAttribute("data-status");
                            var dueDateTarefa = this.getAttribute("data-dueDate");
                            var responsavelTarefa = this.getAttribute("data-responsavel");
                            var projetoTarefa = this.getAttribute("data-projeto");

                            modalTitle.textContent = "Editar Tarefa";
                            submitBtn.textContent = "Salvar Alterações";

                            taskId.value = id;
                            descricao.value = descricaoTarefa;
                            status.value = statusTarefa;
                            dueDate.value = dueDateTarefa;
                            responsavel.value = responsavelTarefa;
                            projeto.value = projetoTarefa;

                            modal.style.display = "block";
                        }
                    });

                    document.getElementById("taskForm").onsubmit = function(event) {
                        event.preventDefault();

                        var formData = new URLSearchParams(new FormData(this));

                        fetch('<%= request.getContextPath() %>/gestor/adicionarTarefa', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: formData
                        })
                        .then(response => response.json())
                        .then(data => {
                            alert(data.message);
                            if (data.message === "Tarefa adicionada com sucesso!" || data.message === "Tarefa atualizada com sucesso!") {
                                modal.style.display = "none";
                                location.reload(); // Atualize a lista de tarefas aqui
                            }
                        })
                        .catch(error => {
                            alert('Erro ao salvar tarefa.');
                        });
                    };
                });
            </script>
  
