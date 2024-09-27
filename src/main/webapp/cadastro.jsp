<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cadastro - Task Management</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .container {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
            width: 500px;
        }

        h2 {
            text-align: center;
        }

        label {
            display: block;
            margin-top: 10px;
            font-weight: bold;
        }

        input[type="text"], input[type="email"], input[type="password"], select {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        input[type="submit"] {
            width: 100%;
            background-color: #28a745;
            color: white;
            padding: 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #218838;
        }

        .error {
            color: red;
            text-align: center;
        }

        .account-question {
            text-align: center;
            margin-top: 20px;
        }

        .account-question a {
            color: #007bff;
            text-decoration: none;
        }

        .account-question a:hover {
            text-decoration: underline;
        }
    </style>
    <script>
        function validateForm() {
            var senha = document.getElementById("senha").value;
            var confirmaSenha = document.getElementById("confirmaSenha").value;

            if (senha !== confirmaSenha) {
                alert("As senhas não coincidem!");
                return false;
            }
            return true;
        }
    </script>
</head>
<body>

<div class="container">
    <h2>Cadastro</h2>

    <%
    String error = request.getParameter("error");
    if (error != null) {
        out.println("<p class='error'>Erro no cadastro. Tente novamente.</p>");
    }
    %>

    <form action="cadastro" method="post" onsubmit="return validateForm()">
        <label for="nome">Nome:</label>
        <input type="text" name="nome" id="nome" required>

        <label for="email">Email:</label>
        <input type="email" name="email" id="email" required>

        <label for="senha">Senha:</label>
        <input type="password" name="senha" id="senha" required>

        <label for="confirmaSenha">Confirmar Senha:</label>
        <input type="password" name="confirmaSenha" id="confirmaSenha" required>

        <label for="papel">Papel:</label>
        <select name="papel" id="papel" required>
            <option value="Gestor">Gestor</option>
        </select>

        <input type="submit" value="Cadastrar">
    </form>

    <!-- Pergunta se já possui uma conta -->
    <div class="account-question">
        Já possui uma conta? <a href="index.jsp">Faça login aqui</a>
    </div>
</div>

</body>
</html>