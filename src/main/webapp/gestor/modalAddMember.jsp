<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<style>
  /* Estilo do modal de membros */
  .membro-modal {
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

  .membro-modal-content {
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

  .membro-close {
      color: #aaa;
      float: right;
      font-size: 28px;
      font-weight: bold;
      cursor: pointer;
  }

  .membro-close:hover,
  .membro-close:focus {
      color: black;
      text-decoration: none;
      cursor: pointer;
  }

  .membro-form-group {
      margin-bottom: 15px;
  }

  .membro-form-group label {
      display: block;
      margin-bottom: 5px;
  }

  .membro-form-group input,
  .membro-form-group textarea {
      width: 100%;
      padding: 8px;
      border: 1px solid #ccc;
      border-radius: 4px;
  }

  .membro-form-group button {
      padding: 10px 20px;
      background-color: #28a745;
      color: white;
      border: none;
      border-radius: 5px;
      cursor: pointer;
  }

  .membro-form-group button:hover {
      background-color: #218838;
  }
</style>
<!-- Modal para Adicionar ou Editar Membro -->
<div id="membroModal" class="membro-modal">
    <div class="membro-modal-content">
        <span class="membro-close" id="closeMembroModalBtn">&times;</span>
        <h2 id="membroModalTitle">Adicionar Novo Membro</h2>
        <form id="membroForm" method="post" action="adicionarMembro">
            <input type="hidden" id="membroId" name="id" value=""> <!-- Campo oculto para ID -->

            <!-- Campo para Nome -->
            <div class="membro-form-group">
                <label for="nome">Nome:</label>
                <input type="text" id="nome" name="nome" required>
            </div>

            <!-- Campo para E-mail -->
            <div class="membro-form-group">
                <label for="email">E-mail:</label>
                <input type="email" id="email" name="email" required>
            </div>

            <!-- Campo de Senha -->
            <div class="membro-form-group">
                <label for="senha">Senha:</label>
                <input type="password" id="senha" name="senha" minlength="6">
            </div>

            <!-- Campo de Confirmar Senha -->
            <div class="membro-form-group">
                <label for="confirmarSenha">Confirmar Senha:</label>
                <input type="password" id="confirmarSenha" name="confirmarSenha" minlength="6">
            </div>

            <!-- Campo de Seleção de Função -->
            <div class="membro-form-group">
                <label for="funcao">Função:</label>
                <select id="funcao" name="funcao" required>
                    <%
                        // Atribua a lista de funções do servlet à variável 'roles'
                        List<String> roles = (List<String>) request.getAttribute("roles");
                        for (String role : roles) {
                    %>
                    <option value="<%= role %>"><%= role %></option>
                    <%
                        }
                    %>
                </select>
            </div>

            <!-- Campo de Seleção de Projeto -->
            <div class="membro-form-group">
                <label for="projeto">Projeto:</label>
                <select id="projeto" name="projeto" required>
                    <%
                        // Atribua a lista de projetos do servlet à variável 'projetos'
                        List<String> projetos = (List<String>) request.getAttribute("projetos");
                        for (String projeto : projetos) {
                    %>
                    <option value="<%= projeto %>"><%= projeto %></option>
                    <%
                        }
                    %>
                </select>
            </div>

            <!-- Botão de Envio -->
            <div class="membro-form-group">
                <button type="submit" id="submitMembroBtn">Adicionar Membro</button>
            </div>
        </form>
    </div>
</div>


<script>
    document.addEventListener('DOMContentLoaded', function() {
        var modal = document.getElementById("membroModal");
        var openModalBtn = document.getElementById("openMembroModalBtn");
         var membroId = document.getElementById("membroId");
                var nome = document.getElementById("nome");
                var email = document.getElementById("email");
                var senha = document.getElementById("senha");
                var confirmarSenha = document.getElementById("confirmarSenha");
                var funcao = document.getElementById("funcao");
                var projeto = document.getElementById("projeto");
                var modalTitle = document.getElementById("membroModalTitle");
                var submitBtn = document.getElementById("submitMembroBtn");
        var closeModalBtn = document.getElementById("closeMembroModalBtn");


        openModalBtn.onclick = function() {
            modalTitle.textContent = "Adicionar Novo Membro";
            submitBtn.textContent = "Adicionar Membro";
            membroId.value = "";
            nome.value = "";
            email.value = "";
            senha.value = "";
            confirmarSenha.value = "";
            funcao.value = "";
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

        var editMembroBtns = document.querySelectorAll(".editMemberBtn");
        editMembroBtns.forEach(function(btn) {
            btn.onclick = function() {
                var id = this.getAttribute("data-id");
                var nomeMembro = this.getAttribute("data-nome");
                var emailMembro = this.getAttribute("data-email");
                var funcaoMembro = this.getAttribute("data-funcao");
                var projetoMembro = this.getAttribute("data-projeto");

                modalTitle.textContent = "Editar Membro";
                submitBtn.textContent = "Salvar Alterações";

                membroId.value = id;
                nome.value = nomeMembro;
                email.value = emailMembro;
                funcao.value = funcaoMembro;
                projeto.value = projetoMembro;

                modal.style.display = "block";
            }
        });

        document.getElementById("membroForm").onsubmit = function(event) {
            event.preventDefault();

            var formData = new URLSearchParams(new FormData(this));

            fetch('<%= request.getContextPath() %>/gestor/adicionarMembro', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                if (data.message === "Membro adicionado com sucesso!" || data.message === "Membro atualizado com sucesso!") {
                    modal.style.display = "none";
                    location.reload(); // Atualize a lista de membros aqui
                }
            })
            .catch(error => {
                alert('Erro ao salvar membro.');
            });
        };

    });
</script>
