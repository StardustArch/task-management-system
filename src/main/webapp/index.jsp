<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login - Task Management</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .login-container {
            background-color: #fff;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 400px;
            text-align: center;
        }

        h2 {
            margin-bottom: 20px;
            color: #333;
        }

        label {
            display: block;
            text-align: left;
            margin-bottom: 5px;
            font-weight: bold;
            color: #333;
        }

        input[type="email"], input[type="password"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 16px;
            box-sizing: border-box;
        }

        input[type="submit"] {
            width: 100%;
            padding: 10px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }

        input[type="submit"]:hover {
            background-color: #218838;
        }

        .error-message {
            color: red;
            margin-bottom: 20px;
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

        /* Para dispositivos móveis */
        @media (max-width: 600px) {
            .login-container {
                padding: 20px;
                width: 90%;
            }
        }
    </style>
</head>
<body>

<div class="login-container">
    <h2>Login</h2>

    <%
    String error = request.getParameter("error");
    if (error != null) {
        if ("invalidCredentials".equals(error)) {
            out.println("<p class='error-message'>Credenciais inválidas. Tente novamente.</p>");
        } else if ("invalidRole".equals(error)) {
            out.println("<p class='error-message'>Papel do usuário inválido.</p>");
        }
    }
    %>

    <form action="login" method="post">
        <label for="email">Email:</label>
        <input type="email" name="email" id="email" required>

        <label for="senha">Senha:</label>
        <input type="password" name="senha" id="senha" required>

        <input type="submit" value="Entrar">
    </form>

    <!-- Pergunta se não possui uma conta -->
    <div class="account-question">
        Não possui uma conta? <a href="cadastro.jsp">Cadastre-se aqui</a>
    </div>
</div>

</body>
</html>