<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>

                .modal {
                    display: none;
                    position: fixed;
                    z-index: 1;
                    left: 0;
                    top: 0;
                    width: 100%;
                    height: 100%;
                    background-color: rgba(0, 0, 0, 0.5);
                }

                .modal-content {
                    background-color: #fff;
                    margin: 15% auto;
                    padding: 20px;
                    border: 1px solid #888;
                    width: 50%;
                    border-radius: 8px;
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

                .form-group input, .form-group textarea {
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
                .dashboard-button {
                    display: inline-block;
                    margin-bottom: 20px;
                    padding: 10px 20px;
                    background-color: #007bff;
                    color: white;
                    text-decoration: none;
                    border-radius: 5px;
                }

                .dashboard-button:hover {
                    background-color: #0056b3;
                }

</style>
<!-- Modal para Adicionar ou Editar Projeto -->
<div id="myModal" class="modal">
    <div class="modal-content">
        <span class="close" id="closeModalBtn">&times;</span>
        <h2 id="modalTitle">Adicionar Novo Projeto</h2>
        <form id="projectForm">
            <input type="hidden" id="projectId" name="id" value=""> <!-- Campo oculto para ID -->

            <div class="form-group">
                <label for="nome">Nome do Projeto:</label>
                <input type="text" id="nome" name="nome" required>
            </div>
            <div class="form-group">
                <label for="descricao">Descrição:</label>
                <textarea id="descricao" name="descricao" required></textarea>
            </div>
            <div class="form-group">
                <label for="prazo">Prazo:</label>
                <input type="date" id="prazo" name="prazo" required>
            </div>
            <div class="form-group">
                <button type="submit" id="submitBtn">Adicionar Projeto</button>
            </div>
        </form>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        var modal = document.getElementById("myModal");
        var openModalBtn = document.getElementById("openModalBtn");
        var closeModalBtn = document.getElementById("closeModalBtn");
        var projectId = document.getElementById("projectId");
        var nome = document.getElementById("nome");
        var descricao = document.getElementById("descricao");
        var prazo = document.getElementById("prazo");
        var modalTitle = document.getElementById("modalTitle");
        var submitBtn = document.getElementById("submitBtn");

        openModalBtn.onclick = function() {
            modalTitle.textContent = "Adicionar Novo Projeto";
            submitBtn.textContent = "Adicionar Projeto";
            projectId.value = "";
            nome.value = "";
            descricao.value = "";
            prazo.value = "";
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

        var editProjectBtns = document.querySelectorAll(".editProjectBtn");
        editProjectBtns.forEach(function(btn) {
            btn.onclick = function() {
                var id = this.getAttribute("data-id");
                var nomeProjeto = this.getAttribute("data-nome");
                var descricaoProjeto = this.getAttribute("data-descricao");
                var prazoProjeto = this.getAttribute("data-prazo");

                modalTitle.textContent = "Editar Projeto";
                submitBtn.textContent = "Salvar Alterações";

                projectId.value = id;
                nome.value = nomeProjeto;
                descricao.value = descricaoProjeto;
                prazo.value = prazoProjeto;

                modal.style.display = "block";
            }
        });

        document.getElementById("projectForm").onsubmit = function(event) {
            event.preventDefault();

            var formData = new URLSearchParams(new FormData(this));

            fetch('<%= request.getContextPath() %>/gestor/adicionarProjecto', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                if (data.message === "Projeto salvo com sucesso!") {
                    modal.style.display = "none";
                    location.reload(); // Atualize a lista de projetos aqui
                }
            })
            .catch(error => {
                alert('Erro ao salvar projeto.');
            });
        };

    });
</script>
