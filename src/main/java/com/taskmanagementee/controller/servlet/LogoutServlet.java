package com.taskmanagementee.controller.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Invalidar a sessão atual
        HttpSession session = request.getSession(false); // Obtém a sessão atual, sem criar uma nova
        if (session != null) {
            session.invalidate(); // Invalida a sessão
        }

        // Redirecionar para a página de login
        response.sendRedirect("index.jsp");
    }
}

