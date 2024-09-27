        package com.taskmanagementee.controller.servlet;

        import com.taskmanagementee.model.DAO.DatabaseConnection;
        import com.taskmanagementee.model.DAO.ProjectoDAO;
        import com.taskmanagementee.model.Projecto;
        import com.taskmanagementee.model.Usuario;
        import jakarta.servlet.ServletException;
        import jakarta.servlet.annotation.WebServlet;
        import jakarta.servlet.http.HttpServlet;
        import jakarta.servlet.http.HttpServletRequest;
        import jakarta.servlet.http.HttpServletResponse;
        import jakarta.servlet.http.HttpSession;

        import java.io.IOException;
        import java.sql.Connection;
        import java.sql.SQLException;
        import java.util.List;

        @WebServlet("/gestor/projectos")
        public class ProjectoListServlet extends HttpServlet {

            private ProjectoDAO projectoDAO;

            @Override
            public void init() throws ServletException {
                // Inicializa a conexão e o DAO
                Connection conn = null;
                try {
                    conn = DatabaseConnection.getConnection();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                projectoDAO = new ProjectoDAO(conn);
            }

            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {

                // Obtém a sessão do usuário
                HttpSession session = request.getSession();
                if (session.getAttribute("usuario") == null) {
                    response.sendRedirect("login.jsp");
                    return;
                }
                Usuario usuario = (Usuario) session.getAttribute("usuario");
                try {
                    // Busca todos os projetos
                    List<Projecto> projectos = projectoDAO.buscarProjetosPorGestor(usuario.getId());

                    // Define os projetos como atributo da requisição
                    request.setAttribute("projectos", projectos);

                    // Encaminha a requisição para a página JSP de listagem de projetos
                    request.getRequestDispatcher("/gestor/listarProjectos.jsp").forward(request, response);

                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect("erro.jsp");
                }
            }
        }

