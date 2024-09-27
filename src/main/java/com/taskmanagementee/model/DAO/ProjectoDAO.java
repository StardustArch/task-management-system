package com.taskmanagementee.model.DAO;
import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.Projecto;
import com.taskmanagementee.model.Tarefa;
import com.taskmanagementee.model.TarefaProjetoDetalhes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectoDAO {

    private Connection connection;
    public ProjectoDAO(Connection connection) {

        this.connection = connection;
    }



    public List<Projecto> buscarProjetosPorGestor(int gestorId) {
            List<Projecto> projetos = new ArrayList<>();
            String sql = "SELECT * FROM projetos WHERE gestor_id = ?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setInt(1, gestorId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Projecto projeto = new Projecto( rs.getString("nome"),
                            rs.getString("descricao"),
                            rs.getDate("prazo").toLocalDate(),
                            rs.getInt("gestor_id")
                    );
                    if(rs.getDate("data_criacao") != null){
                        projeto.setData_criacao(rs.getDate("data_criacao").toLocalDate());
                    }

                    projeto.setId(rs.getInt("id"));
                    projetos.add(projeto);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return projetos;
        }
        public List<String> buscarNomesProjetosPorGestor(int gestorId) {
            List<Projecto> projetos = buscarProjetosPorGestor(gestorId);
            List<String> nomesProjetos = new ArrayList<>();

            for (Projecto projeto : projetos) {
                nomesProjetos.add(projeto.getNome());
            }

            return nomesProjetos;
        }

    public Date getUltimaAtualizacao(int projetoId) throws SQLException {
        Date ultimaAtualizacao = null;
        String sql = "SELECT MAX(data_conclusao) AS ultima_atualizacao " +
                "FROM tarefas " +
                "WHERE projeto_id = ? AND (status = 'Concluida' OR status = 'Em Andamento')";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, projetoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ultimaAtualizacao = rs.getDate("ultima_atualizacao");
            }
        }

        return ultimaAtualizacao;
    }

    // Método para buscar as tarefas atribuídas a um membro em um projeto específico
    public List<TarefaProjetoDetalhes> getTarefasAtribuidas(int membroId, int projetoId) throws SQLException {
        List<TarefaProjetoDetalhes> tarefasAtribuidas = new ArrayList<>();

        // Query SQL para buscar as tarefas e informações adicionais
        String sql = "SELECT t.descricao AS tarefa_descricao, t.status, t.prazo AS tarefa_prazo, " +
                "IFNULL(t.data_conclusao, 'Tarefa ainda por ser concluída') AS data_conclusao, " +
                "p.nome AS projeto_nome, p.descricao AS projeto_descricao, u_gestor.nome AS gestor_nome " +
                "FROM tarefas t " +
                "JOIN projetos p ON t.projeto_id = p.id " +
                "JOIN usuarios u ON t.responsavel_id = u.id " +
                "JOIN usuarios u_gestor ON p.gestor_id = u_gestor.id " +
                "WHERE u.id = ? AND p.id = ?";

        // Prepara a query para execução
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, membroId);  // Substitui o primeiro ? pelo ID do membro
            stmt.setInt(2, projetoId); // Substitui o segundo ? pelo ID do projeto

            // Executa a query
            try (ResultSet rs = stmt.executeQuery()) {
                // Processa os resultados
                while (rs.next()) {
                    String tarefaDescricao = rs.getString("tarefa_descricao");
                    String status = rs.getString("status");
                    Date tarefaPrazo = rs.getDate("tarefa_prazo");
                    String dataConclusao = rs.getString("data_conclusao");
                    String projetoNome = rs.getString("projeto_nome");
                    String projetoDescricao = rs.getString("projeto_descricao");
                    String gestorNome = rs.getString("gestor_nome");

                    // Cria o objeto com os detalhes completos da tarefa e projeto
                    TarefaProjetoDetalhes tarefaProjetoDetalhes = new TarefaProjetoDetalhes(
                            tarefaDescricao, status, tarefaPrazo, dataConclusao, projetoNome, projetoDescricao, gestorNome
                    );

                    tarefasAtribuidas.add(tarefaProjetoDetalhes);
                }
            }
        }
        return tarefasAtribuidas;
    }

    // Método para buscar todos os projetos onde um membro está envolvido
    public List<Projecto> getProjetosPorMembro(int membroId) throws SQLException {
        List<Projecto> projetos = new ArrayList<>();

        String sql = "SELECT DISTINCT p.id, p.nome, p.descricao, u.nome AS gestor_nome " +
                "FROM projetos p " +
                "JOIN tarefas t ON p.id = t.projeto_id " +
                "JOIN usuarios u ON p.gestor_id = u.id " +
                "WHERE t.responsavel_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, membroId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Projecto projeto = new Projecto();
                    projeto.setId(rs.getInt("id"));
                    projeto.setNome(rs.getString("nome"));
                    projeto.setDescricao(rs.getString("descricao"));
                    projeto.setGestorNome(rs.getString("gestor_nome"));

                    projetos.add(projeto);
                }
            }
        }

        return projetos;
    }

    // Método para contar o número de projetos ativos onde um membro está envolvido
    public int contarProjetosAtivosPorMembro(int membroId) throws SQLException {
        int numeroProjetosAtivos = 0;

        String sql = "SELECT COUNT(DISTINCT p.id) AS total_ativos " +
                "FROM projetos p " +
                "JOIN tarefas t ON p.id = t.projeto_id " +
                "WHERE t.responsavel_id = ? " +
                "AND (t.status = 'Pendente' OR t.status = 'Em Andamento')";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, membroId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    numeroProjetosAtivos = rs.getInt("total_ativos");
                }
            }
        }

        return numeroProjetosAtivos;
    }


    public boolean salvarProjeto(Projecto projeto) {
            String sql = "INSERT INTO projetos (nome, descricao, prazo, gestor_id, data_criacao) VALUES (?, ?, ?, ?, CURDATE())";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, projeto.getNome());
                stmt.setString(2, projeto.getDescricao());
                stmt.setDate(3, java.sql.Date.valueOf(projeto.getPrazo()));
                // Defina o ID do gestor. Substitua com o valor real ou adicione um parâmetro se necessário.
                stmt.setInt(4, projeto.getGestorId());

                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();

            }
            return false;
        }
        public boolean atualizarProjeto(Projecto projeto) {
            String sql = "UPDATE projetos SET nome = ?, descricao = ?, prazo = ? WHERE id = ?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                // Preenche os parâmetros da consulta
                stmt.setString(1, projeto.getNome());
                stmt.setString(2, projeto.getDescricao());
                stmt.setDate(3, java.sql.Date.valueOf(projeto.getPrazo()));
                stmt.setInt(4, projeto.getId()); // ID do projeto a ser atualizado

                // Executa a atualização
                int rowsAffected = stmt.executeUpdate();

                // Retorna true se pelo menos uma linha foi afetada (ou seja, o projeto foi atualizado)
                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
        public boolean excluirProjeto(int projeto_id) {
            String sql = "DELETE FROM projetos WHERE id = ?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setInt(1, projeto_id);
                int rowsAffected = stmt.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        public int getProjetoIdByName(String nomeProjeto) {
            String sql = "SELECT id FROM projetos WHERE nome = ?";
            int projetoId = -1;

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, nomeProjeto);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    projetoId = rs.getInt("id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return projetoId;
        }

    public int getTotalProjetosAtivos(int gestorId) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT p.id) AS total FROM projetos p " +
                "JOIN tarefas t ON p.id = t.projeto_id " +
                "WHERE t.status IN ('Pendente', 'Em Andamento') " +
                "AND p.gestor_id = ?";

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
            throw new SQLException("Erro ao obter total de projetos ativos.");
        }
        return 0;
    }

    public Projecto buscarPorId(int id) {
        Projecto projecto = null;
        String sql = "SELECT * FROM projetos WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                projecto = new Projecto();
                projecto.setId(rs.getInt("id"));
                projecto.setNome(rs.getString("nome"));
                projecto.setDescricao(rs.getString("descricao"));
                projecto.setPrazo(rs.getDate("prazo").toLocalDate());
                projecto.setGestorId(rs.getInt("gestor_id"));
                projecto.setData_criacao(rs.getDate("data_criacao").toLocalDate());

            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projecto;
    }
    // Buscar projeto por ID
    public Projecto buscarPorId2(int id) throws SQLException {
        String sql = "SELECT p.id, p.nome, p.descricao, p.prazo,p.data_criacao, u.nome AS gestor_nome " +
                "FROM projetos p " +
                "JOIN usuarios u ON p.gestor_id = u.id WHERE p.id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Projecto projecto = new Projecto();
                projecto.setId(resultSet.getInt("id"));
                projecto.setNome(resultSet.getString("nome"));
                projecto.setDescricao(resultSet.getString("descricao"));
                projecto.setPrazo(resultSet.getDate("prazo").toLocalDate());
                projecto.setGestorNome(resultSet.getString("gestor_nome"));
                projecto.setData_criacao(resultSet.getDate("data_criacao").toLocalDate());
                return projecto;
            }
        }
        return null;
    }

    // Obter tarefas de um projeto
    public List<Tarefa> obterTarefasDoProjeto(int projectId) throws SQLException {
        String sql = "SELECT t.id, t.descricao, t.status, t.prazo, u.nome AS responsavel_nome " +
                "FROM tarefas t " +
                "JOIN usuarios u ON t.responsavel_id = u.id " +
                "WHERE t.projeto_id = ?";
        List<Tarefa> tarefas = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Tarefa tarefa = new Tarefa();
                tarefa.setId(resultSet.getInt("id"));
                tarefa.setDescricao(resultSet.getString("descricao"));
                tarefa.setStatus(resultSet.getString("status"));
                tarefa.setPrazo(resultSet.getDate("prazo").toLocalDate());
                tarefa.setResponsavelNome(resultSet.getString("responsavel_nome"));
                tarefas.add(tarefa);
            }
        }
        return tarefas;
    }

    // Contar tarefas por status
    public int contarTarefasPorStatus(int projectId, String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tarefas WHERE projeto_id = ? AND status = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId);
            statement.setString(2, status);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    // Contar total de tarefas
    public int contarTotalTarefas(int projectId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tarefas WHERE projeto_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }


}
