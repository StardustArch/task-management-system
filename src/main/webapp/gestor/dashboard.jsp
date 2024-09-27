<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.taskmanagementee.model.Usuario" %>
<%@ page session="true" %>
<html lang="pt">
<head>
    <meta charset="UTF-8">
    <title>Painel do Gestor - Task Management</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
            height: 100vh;
            position: relative;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        h1, h2 {
            color: #333;
        }
        .nav {
            margin-bottom: 30px;
        }
        .nav a {
            margin-right: 20px;
            text-decoration: none;
            color: #007bff;
            font-weight: bold;
        }
        .nav a:hover {
            text-decoration: underline;
        }
        .logout {
            float: right;
            font-weight: bold;
        }
        .info-box {
            display: flex;
            justify-content: space-between;
            margin-bottom: 30px;
        }
        .info-box div {
            background-color: #007bff;
            color: white;
            border-radius: 8px;
            padding: 20px;
            text-align: center;
            flex: 1;
            margin-right: 20px;
        }
        .info-box div:last-child {
            margin-right: 0;
        }
        .info-box h3 {
            margin: 0 0 10px 0;
            font-size: 18px;
        }
        .info-box p {
            margin: 0;
            font-size: 16px;
            font-weight: bold;
        }

        /* Estilizando o botão de ações rápidas */
        .dropup {
            position: fixed;
            right: 20px;
            bottom: 20px;
            z-index: 100;
        }
        .dropup button {
            background-color: #28a745;
            color: white;
            padding: 15px;
            border-radius: 50%;
            border: none;
            font-size: 18px;
            cursor: pointer;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: background-color 0.3s ease;
        }
        .dropup button:hover {
            background-color: #218838;
        }
        .dropup-content {
            display: none;
            position: absolute;
            bottom: 60px; /* Posição acima do botão */
            right: 0;
            background-color: #f9f9f9;
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
            border-radius: 8px;
            min-width: 160px;
            padding: 10px 0;
            z-index: 1;
        }
        .dropup-content a {
            color: black;
            padding: 12px 16px;
            text-decoration: none;
            display: block;
            text-align: left;
        }
        .dropup-content a:hover {
            background-color: #ddd;
        }
        .show {
            display: block;
        }
/* Estilos do modal exclusivo */
.modal-unique {
    display: none; /* Modal escondido inicialmente */
    position: fixed; /* Fixa o modal na tela */
    z-index: 1; /* Traz o modal para o topo */
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5); /* Fundo escuro */
    justify-content: center; /* Centraliza horizontalmente */
    align-items: center; /* Centraliza verticalmente */
    overflow: hidden; /* Impede rolagem */
}

.modal-content-unique {
    background-color: #fff;
    padding: 20px;
    border: 1px solid #888;
    width: 50%;
    max-width: 600px; /* Limita o tamanho do modal */
    border-radius: 8px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
    position: relative; /* Garante que o conteúdo do modal seja relativo ao centro */
}

.close-unique {
    color: #aaa;
    float: right;
    font-size: 28px;
    font-weight: bold;
    cursor: pointer;
}

.close-unique:hover,
.close-unique:focus {
    color: black;
    text-decoration: none;
}

/* Outros estilos de formulário */
label {
    display: block;
    margin: 10px 0 5px;
    font-weight: bold;
}

input[type="text"], input[type="email"], input[type="password"] {
    width: 100%;
    padding: 10px;
    margin-bottom: 20px;
    border: 1px solid #ccc;
    border-radius: 4px;
}

button {
    background-color: #28a745;
    color: white;
    padding: 10px 15px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

button:hover {
    background-color: #218838;
}
    </style>
</head>
<body>

<div class="container">
    <div class="nav">
        <a href="<%= request.getContextPath() %>/gestor/dashboard">Início</a>
        <a href="<%= request.getContextPath() %>/gestor/membros">Gerenciar Membros</a>
        <a href="<%= request.getContextPath() %>/gestor/projectos">Projetos</a>
        <a href="<%= request.getContextPath() %>/gestor/tarefas ">Tarefas</a>
        <a href="../logout" class="logout">Sair</a>
    </div>

    <h1>Bem-vindo ao Painel do Gestor!</h1>

        <%
        // Obtém o usuário da sessão
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario != null) {
        %>
        <p>Olá, <strong><%= usuario.getNome() %></strong>! Você está autenticado como <strong>Gestor</strong>.</p>
        <%
        } else {
        // Se o usuário não estiver autenticado, redireciona para a página de login
        response.sendRedirect("index.jsp");
        }
        %>
        <button id="editProfileBtn">Editar Perfil</button>



      <!-- Modal de Edição de Perfil -->
      <div id="editProfileModalUnique" class="modal-unique">
          <div class="modal-content-unique">
              <span class="close-unique">&times;</span>
              <h2>Editar Informações de Perfil</h2>
              <form action="<%= request.getContextPath() %>/gestor/atualizarPerfil" method="POST">
                  <label for="nomeUnique">Nome</label>
                  <input type="text" id="nomeUnique" name="nome" value="<%= usuario.getNome() %>" required>

                  <label for="emailUnique">Email</label>
                  <input type="email" id="emailUnique" name="email" value="<%= usuario.getEmail() %>" \>

                  <label for="senhaAtualUnique">Senha Atual</label>
                  <input type="password" id="senhaAtualUnique" name="senhaAtual">

                  <label for="novaSenhaUnique">Nova Senha (opcional)</label>
                  <input type="password" id="novaSenhaUnique" name="novaSenha">

                  <button type="submit">Salvar Alterações</button>
              </form>
          </div>
      </div>


        <script>
           // Abrir o modal ao clicar no botão
           var modalUnique = document.getElementById("editProfileModalUnique");
           var btnUnique = document.getElementById("editProfileBtn"); // Certifique-se de que o botão tenha esse ID
           var spanUnique = document.getElementsByClassName("close-unique")[0];

           // Verificar se o botão existe na página para evitar erros
           if (btnUnique) {
               btnUnique.onclick = function() {
                   modalUnique.style.display = "flex"; // Mostrar modal com display flex para centralizar
               }
           }

           // Fechar o modal ao clicar no "X"
           spanUnique.onclick = function() {
               modalUnique.style.display = "none";
           }

           // Fechar o modal ao clicar fora do conteúdo
           window.onclick = function(event) {
               if (event.target == modalUnique) {
                   modalUnique.style.display = "none";
               }
           }
        </script>







    <!-- Conteúdo Geral da Página -->
    <h2>Informações Gerais</h2>
    <div class="info-box">
        <div>
            <h3>Total de Membros</h3>
            <p><%= request.getAttribute("totalMembros") != null ? request.getAttribute("totalMembros") : "Valor não disponível" %></p>
        </div>
        <div>
            <h3>Projetos Ativos</h3>
            <p><%= request.getAttribute("totalProjetosAtivos") != null ? request.getAttribute("totalProjetosAtivos") : "Valor não disponível" %></p>
        </div>
        <div>
            <h3>Tarefas Pendentes</h3>
            <p><%= request.getAttribute("totalTarefasPendentes") != null ? request.getAttribute("totalTarefasPendentes") : "Valor não disponível" %></p>
        </div>
    </div>
</div>

<!-- Botão Dropup para Ações Rápidas -->
<div class="dropup">
    <button onclick="toggleDropup()">☰</button>
    <div class="dropup-content" id="dropupMenu">
        <a href="#" id="openMembroModalBtn">Adicionar Novo Membro</a>
        <a href="#" id="openModalBtn">Criar Novo Projeto</a>
        <a href="#" id="openTaskModalBtn">Atribuir Tarefa</a>
    </div>
</div>

<%@ include file="modalAddMember.jsp" %>
<%@ include file="modalAddProject.jsp" %>
<%@ include file="modalAddTarefa.jsp" %>

<script>
    // Função para abrir/fechar o menu dropup
    function toggleDropup() {
        document.getElementById("dropupMenu").classList.toggle("show");
    }

    // Fechar o dropup se o usuário clicar fora dele
    window.onclick = function(event) {
        if (!event.target.matches('button')) {
            var dropups = document.getElementsByClassName("dropup-content");
            for (var i = 0; i < dropups.length; i++) {
                var openDropup = dropups[i];
                if (openDropup.classList.contains('show')) {
                    openDropup.classList.remove('show');
                }
            }
        }
    }
</script>

</body>
</html>