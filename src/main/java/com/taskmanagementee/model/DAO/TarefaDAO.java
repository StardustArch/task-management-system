package com.taskmanagementee.model.DAO;

import com.taskmanagementee.model.Tarefa;
import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.TarefaProjetoDetalhes;


import java.sql.*;
import java.time.LocalDate;


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public class TarefaDAO {

    private Connection connection;
    public TarefaDAO(Connection connection) {

        this.connection = connection;
    }

        // Método para buscar todas as tarefas
        public ArrayList<Tarefa> getAllTarefas() {
            ArrayList<Tarefa> tarefasData = new ArrayList<Tarefa>();
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                connection = DatabaseConnection.getConnection(); // Obtendo a conexão com o banco

                String sql = "SELECT t.id, t.descricao, t.status, t.prazo, u.nome AS responsavel,u.id AS reponsavel_id, p.nome AS projeto,  p.id AS projecto_id " +
                        "FROM tarefas t " +
                        "JOIN usuarios u ON t.responsavel_id = u.id " +
                        "JOIN projetos p ON t.projeto_id = p.id";

                preparedStatement = connection.prepareStatement(sql);
                resultSet = preparedStatement.executeQuery();

                // Adiciona as tarefas no ObservableList
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String descricao = resultSet.getString("descricao");
                    String status = resultSet.getString("status");
                    Date prazo = resultSet.getDate("prazo");
                    String responsavel = resultSet.getString("responsavel");
//                int responsavel_id = resultSet.getInt("reponsavel_id");
                    String projeto = resultSet.getString("projeto");
//                int projecto_id = resultSet.getInt("projecto_id");


                    tarefasData.add(new Tarefa(id, descricao, status, prazo != null ? prazo.toLocalDate() : null, responsavel, projeto));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Fechar as conexões
                try {
                    if (resultSet != null) resultSet.close();
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return tarefasData;
        }
    public void atualizarStatusTarefa(int tarefaId, String novoStatus) throws SQLException {
        String sql = "UPDATE tarefas SET status = ?, data_conclusao = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoStatus);

            // Define a data de conclusão apenas se a tarefa for marcada como concluída
            if ("Concluida".equals(novoStatus)) {
                stmt.setDate(2, Date.valueOf(LocalDate.now())); // Data atual
            } else {
                stmt.setNull(2, java.sql.Types.DATE); // Limpa a data de conclusão caso não esteja concluída
            }

            stmt.setInt(3, tarefaId);
            stmt.executeUpdate();
        }
    }

    public List<Tarefa> getTarefasByProjetoId(int projetoId) {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT t.id, t.descricao, t.status, t.prazo, " +
                "u.nome AS responsavel, u.id AS responsavel_id, " +
                "p.nome AS projeto, p.id AS projeto_id, t.data_conclusao " +
                "FROM tarefas t " +
                "JOIN usuarios u ON t.responsavel_id = u.id " +
                "JOIN projetos p ON t.projeto_id = p.id " +
                "WHERE p.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, projetoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Tarefa tarefa = new Tarefa();
                tarefa.setId(rs.getInt("id"));
                tarefa.setDescricao(rs.getString("descricao"));
                tarefa.setStatus(rs.getString("status"));
                tarefa.setPrazo(rs.getDate("prazo").toLocalDate());
                tarefa.setResponsavelNome(rs.getString("responsavel"));
                tarefa.setResponsavel_id(rs.getInt("responsavel_id"));
                tarefa.setProjeto(rs.getString("projeto"));
                tarefa.setProjecto_id(rs.getInt("projeto_id"));
                tarefa.setDataConclusao(rs.getDate("data_conclusao")); // Pega a data de conclusão
                tarefas.add(tarefa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tarefas;
    }


    // Método para buscar todas as tarefas de um determinado responsável
    public List<Tarefa> getTarefasPorResponsavel(int responsavelId) throws SQLException {
        List<Tarefa> tarefas = new ArrayList<>();

        String sql = "SELECT t.id, t.descricao, t.status, t.prazo,p.gestor_id,t.data_conclusao, p.nome AS projeto_nome " +
                "FROM tarefas t " +
                "JOIN projetos p ON t.projeto_id = p.id " +
                "WHERE t.responsavel_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, responsavelId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Tarefa tarefa = new Tarefa();
                    tarefa.setId(rs.getInt("id"));
                    tarefa.setDescricao(rs.getString("descricao"));
                    tarefa.setStatus(rs.getString("status"));
                    tarefa.setPrazo(rs.getDate("prazo").toLocalDate());
                    tarefa.setProjeto(rs.getString("projeto_nome"));
                    tarefa.setDataConclusao(rs.getDate("data_conclusao"));
                    MembroDAO membroDAO = new MembroDAO(connection);
                    List<String> gestor = membroDAO.getGestorById(rs.getInt("gestor_id"));
                    tarefa.setGestor(gestor.get(0));
                    tarefa.setGestorEmail(gestor.get(1));

                    tarefas.add(tarefa);
                }
            }
        }

        return tarefas;
    }

    public ArrayList<Tarefa> getAllTarefasPorGestor(int gestorId) {
        ArrayList<Tarefa> tarefasData = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection(); // Obtendo a conexão com o banco

            // Consulta SQL modificada para incluir o filtro de gestor
            String sql = "SELECT t.id, t.descricao, t.status, t.prazo, u.nome AS responsavel, u.id AS responsavel_id, " +
                    "p.nome AS projeto, p.id AS projeto_id " +
                    "FROM tarefas t " +
                    "JOIN usuarios u ON t.responsavel_id = u.id " +
                    "JOIN projetos p ON t.projeto_id = p.id " +
                    "WHERE p.gestor_id = ?"; // Filtro para o ID do gestor

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, gestorId); // Definir o parâmetro do ID do gestor
            resultSet = preparedStatement.executeQuery();

            // Adiciona as tarefas no ArrayList
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String descricao = resultSet.getString("descricao");
                String status = resultSet.getString("status");
                Date prazo = resultSet.getDate("prazo");
                String responsavel = resultSet.getString("responsavel");
                String projeto = resultSet.getString("projeto");

                tarefasData.add(new Tarefa(id, descricao, status, prazo != null ? prazo.toLocalDate() : null, responsavel, projeto));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fechar as conexões
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tarefasData;
    }

    public List<Tarefa> getTarefasAtrasadas() throws SQLException {
        List<Tarefa> tarefasAtrasadas = new ArrayList<>();

        String sql = "SELECT * FROM tarefas WHERE prazo < CURDATE() AND status != 'Concluída'";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Tarefa tarefa = new Tarefa();
                tarefa.setId(rs.getInt("id"));
                tarefa.setDescricao(rs.getString("descricao"));
                tarefa.setStatus(rs.getString("status"));
                tarefa.setPrazo(rs.getDate("prazo").toLocalDate());

                tarefasAtrasadas.add(tarefa);
            }
        }

        return tarefasAtrasadas;
    }

    public List<Tarefa> getTarefasAtrasadasPorResponsavel(int responsavelId) throws SQLException {
        List<Tarefa> tarefasAtrasadas = new ArrayList<>();

        String sql = "SELECT * FROM tarefas WHERE prazo < CURDATE() AND status != 'Concluída' AND responsavel_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, responsavelId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Tarefa tarefa = new Tarefa();
                    tarefa.setId(rs.getInt("id"));
                    tarefa.setDescricao(rs.getString("descricao"));
                    tarefa.setStatus(rs.getString("status"));
                    tarefa.setPrazo(rs.getDate("prazo").toLocalDate());

                    tarefasAtrasadas.add(tarefa);
                }
            }
        }

        return tarefasAtrasadas;
    }


    // Método para adicionar uma nova tarefa
        public boolean addTarefa(String descricao, String status, LocalDate prazo, String projeto, String responsavel) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            boolean sucesso = false;



            try {
                connection = DatabaseConnection.getConnection();

                ProjectoDAO projectoDAO = new ProjectoDAO(connection);
                MembroDAO membroDAO = new MembroDAO(connection);
                if(Objects.equals(status, "Concluida")){
                String sql = "INSERT INTO tarefas (descricao, status, prazo, projeto_id, responsavel_id, data_conclusao) VALUES (?, ?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, descricao);
                preparedStatement.setString(2, status);  // "Pendente", "Em Andamento", ou "Concluída"
                preparedStatement.setDate(3, prazo != null ? Date.valueOf(prazo) : null);  // Converte LocalDate para SQL Date
                preparedStatement.setInt(4, projectoDAO.getProjetoIdByName(projeto));
                preparedStatement.setInt(5, membroDAO.getMembroIdByName(responsavel));

                    preparedStatement.setDate(6, prazo != null ? Date.valueOf(prazo) : null);
                }else {
                    String sql = "INSERT INTO tarefas (descricao, status, prazo, projeto_id, responsavel_id) VALUES (?, ?, ?, ?, ?)";
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, descricao);
                    preparedStatement.setString(2, status);  // "Pendente", "Em Andamento", ou "Concluída"
                    preparedStatement.setDate(3, prazo != null ? Date.valueOf(prazo) : null);  // Converte LocalDate para SQL Date
                    preparedStatement.setInt(4, projectoDAO.getProjetoIdByName(projeto));
                    preparedStatement.setInt(5, membroDAO.getMembroIdByName(responsavel));

                }

                int rowsAffected = preparedStatement.executeUpdate();
                sucesso = rowsAffected > 0;


            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return sucesso;
        }

        // Método para editar uma tarefa existente
        public boolean updateTarefa(int tarefaId, String novaDescricao, String novoStatus, LocalDate novoPrazo, String novoProjeto, String novoResponsavel) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            boolean sucesso = false;

            try {
                connection = DatabaseConnection.getConnection();
                String sql = "UPDATE tarefas SET descricao = ?, status = ?, prazo = ?, projeto_id = ?, responsavel_id = ? WHERE id = ?";
                preparedStatement = connection.prepareStatement(sql);
                ProjectoDAO projectoDAO = new ProjectoDAO(connection);
                MembroDAO membroDAO = new MembroDAO(connection);
                preparedStatement.setString(1, novaDescricao);
                preparedStatement.setString(2, novoStatus);
                preparedStatement.setDate(3, novoPrazo != null ? Date.valueOf(novoPrazo) : null);
                preparedStatement.setInt(4, projectoDAO.getProjetoIdByName(novoProjeto));
                preparedStatement.setInt(5, membroDAO.getMembroIdByName(novoResponsavel));
                preparedStatement.setInt(6, tarefaId);

                int rowsAffected = preparedStatement.executeUpdate();
                sucesso = rowsAffected > 0;

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return sucesso;
        }

        // Método para deletar uma tarefa
        public boolean deleteTarefa(int tarefaId) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            boolean sucesso = false;

            try {
                connection = DatabaseConnection.getConnection();
                String sql = "DELETE FROM tarefas WHERE id = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, tarefaId);

                int rowsAffected = preparedStatement.executeUpdate();
                sucesso = rowsAffected > 0;

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return sucesso;
        }

        // Método para buscar uma tarefa por ID
        public Tarefa getTarefaById(int tarefaId) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            Tarefa tarefa = null;

            try {
                connection = DatabaseConnection.getConnection();
                String sql = "SELECT t.id, t.descricao, t.status, t.prazo, u.nome AS responsavel, u.id AS reponsavel_id, p.nome AS projeto,  p.id AS projecto_id " +
                        "FROM tarefas t " +
                        "JOIN usuarios u ON t.responsavel_id = u.id " +
                        "JOIN projetos p ON t.projeto_id = p.id WHERE t.id = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, tarefaId);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String descricao = resultSet.getString("descricao");
                    String status = resultSet.getString("status");
                    Date prazo = resultSet.getDate("prazo");
                    String responsavel = resultSet.getString("responsavel");
//                int responsavel_id = resultSet.getInt("reponsavel_id");
                    String projeto = resultSet.getString("projeto");
//                int projecto_id = resultSet.getInt("projecto_id");

                    tarefa = new Tarefa(id, descricao, status, prazo != null ? prazo.toLocalDate() : null, responsavel, projeto);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultSet != null) resultSet.close();
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return tarefa;
        }


    public int contarTarefasPorUsuario(String responsavel) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tarefas WHERE responsavel_id = ? AND (status = 'Pendente' OR status = 'Em Andamento')";
        MembroDAO membroDAO = new MembroDAO(connection);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, membroDAO.getMembroIdByName(responsavel));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);  // Retorna o número de tarefas
                }
            }
        }
        return 0;  // Retorna 0 se o usuário não tiver tarefas
    }


        public int getTotalTarefasPendentes(int gestorId) throws SQLException {
            String sql = "SELECT COUNT(*) AS total FROM tarefas t " +
                    "JOIN projetos p ON t.projeto_id = p.id " +
                    "WHERE t.status IN ('Pendente', 'Em Andamento') " +
                    "AND p.gestor_id = ?"; // Filtro para o gestor
    
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
                // Define o ID do gestor no parâmetro da consulta
                pstmt.setInt(1, gestorId);
    
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("total");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException("Erro ao obter total de tarefas pendentes.");
            }
            return 0;
        }



}

