package com.taskmanagementee.model.DAO;


import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.Membro;
import com.taskmanagementee.model.Projecto;
import com.taskmanagementee.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class MembroDAO {

    private Connection connection;

    public MembroDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Usuario> getAllMembers() {
        List<Usuario> membrosData = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
                    String sql = "SELECT u.id, u.nome, u.email, " +
                            "COALESCE(mf.funcao, 'Sem Função') AS funcao, " +
                            "u.senha, " +
                            "(SELECT COUNT(*) FROM tarefas WHERE responsavel_id = u.id AND status IN ('em andamento', 'pendente')) AS tarefasAtribuidas " +
                            "FROM usuarios u " +
                            "LEFT JOIN membro_funcoes mf ON u.id = mf.usuario_id " +
                            "WHERE u.papel != 'gestor'";

            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                String email = resultSet.getString("email");
                String senha = resultSet.getString("senha");
                String funcao = resultSet.getString("funcao");
                int tarefasAtribuidas = resultSet.getInt("tarefasAtribuidas");
                String status = tarefasAtribuidas >= 5 ? "Inativo" : "Ativo";
                Usuario novo = new Usuario();
                novo.setNome(nome);
                novo.setEmail(email);
                novo.setSenha(senha);
                novo.setId(id);
                novo.setStatus(status);
                novo.setFuncao_membro(funcao);
                novo.setTarefasAtribuidas(tarefasAtribuidas);
                membrosData.add(novo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return membrosData;
    }

    public boolean deleteMember(int memberId) {
        PreparedStatement deleteFromMembroFuncaoStmt = null;
        PreparedStatement deleteFromUsuariosStmt = null;

        try {
            connection.setAutoCommit(false);

            String deleteMembroFuncaoSql = "DELETE FROM membro_funcoes WHERE usuario_id = ?";
            String deleteUsuarioSql = "DELETE FROM usuarios WHERE id = ?";

            deleteFromMembroFuncaoStmt = connection.prepareStatement(deleteMembroFuncaoSql);
            deleteFromMembroFuncaoStmt.setInt(1, memberId);

            deleteFromUsuariosStmt = connection.prepareStatement(deleteUsuarioSql);
            deleteFromUsuariosStmt.setInt(1, memberId);

            int rowsDeletedMembroFuncao = deleteFromMembroFuncaoStmt.executeUpdate();
            int rowsDeletedUsuarios = deleteFromUsuariosStmt.executeUpdate();

            if (rowsDeletedUsuarios > 0) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (deleteFromMembroFuncaoStmt != null) deleteFromMembroFuncaoStmt.close();
                if (deleteFromUsuariosStmt != null) deleteFromUsuariosStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean addMembro(String nome, String email, String senha, String projeto, String funcao) {
        try {
            addMembroToDatabase(nome, email, senha);
            int usuarioId = getMembroIdByName(nome);
            addMembroFuncao(usuarioId, projeto, funcao);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<String> buscarNomesMembros() {
        List<Usuario> membros = this.getAllMembers();
        List<String> nomesMembros = new ArrayList<>();

        for (Usuario membro : membros) {
            nomesMembros.add(membro.getNome());
        }

        return nomesMembros;
    }

    private void addMembroToDatabase(String nome, String email, String senha) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, senha, papel) VALUES (?, ?, ?, 'Membro')";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.executeUpdate();
        }
    }

    public int getMembroIdByName(String nomeUsuario) {
        String sql = "SELECT id FROM usuarios WHERE nome = ?";
        int usuarioId = -1;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nomeUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usuarioId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarioId;
    }
    public List<String> getGestorById(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        String nome;
        String email;
        List<String> nova = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nome = rs.getString("nome");
                email = rs.getString("email");
                nova.add(nome);
                nova.add(email);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nova;
    }

    private void addMembroFuncao(int usuarioId, String projeto, String funcao) throws SQLException {
        String sql = "INSERT INTO membro_funcoes (usuario_id, projeto_id, funcao) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            int projetoId = getProjectIdByName(projeto);
            pstmt.setInt(2, projetoId);
            pstmt.setString(3, funcao);
            pstmt.executeUpdate();
        }
    }

    private int getProjectIdByName(String nome) throws SQLException {
        String sql = "SELECT id FROM projetos WHERE nome = ?";
        int projetoId = -1;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                projetoId = rs.getInt("id");
            }
        }
        return projetoId;
    }

    public int getTotalMembros() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM usuarios WHERE papel = 'Membro'";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Erro ao obter total de membros.");
        }
        return 0;
    }

    public boolean updateMembro(Usuario membro, String projeto, String funcao) {
        PreparedStatement preparedStatementUsuario = null;
        PreparedStatement preparedStatementFuncao = null;
        PreparedStatement preparedStatementCheck = null;
        ResultSet resultSet = null;

        try {
            connection.setAutoCommit(false);

            // Atualização do usuário
            String sqlUpdateUsuario = "UPDATE usuarios SET nome = ?, email = ?, senha = ? WHERE id = ?";
            preparedStatementUsuario = connection.prepareStatement(sqlUpdateUsuario);
            preparedStatementUsuario.setString(1, membro.getNome());
            preparedStatementUsuario.setString(2, membro.getEmail());
            preparedStatementUsuario.setString(3, membro.getSenha());
            preparedStatementUsuario.setInt(4, membro.getId());

            // Execute a atualização do usuário
            int rowsUpdatedUsuario = preparedStatementUsuario.executeUpdate();
            System.out.println("Rows updated in usuarios: " + rowsUpdatedUsuario);

            // Verificar se já existe um registro na tabela membro_funcoes
            String sqlCheckFuncao = "SELECT COUNT(*) FROM membro_funcoes WHERE usuario_id = ?";
            preparedStatementCheck = connection.prepareStatement(sqlCheckFuncao);
            preparedStatementCheck.setInt(1, membro.getId());
            resultSet = preparedStatementCheck.executeQuery();

            boolean funcaoUpdated = false;
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                // Se já existe, atualizar a função
                String sqlUpdateFuncao = "UPDATE membro_funcoes SET funcao = ? WHERE usuario_id = ?";
                preparedStatementFuncao = connection.prepareStatement(sqlUpdateFuncao);
                preparedStatementFuncao.setString(1, funcao);
                preparedStatementFuncao.setInt(2, membro.getId());
                int rowsUpdatedFuncao = preparedStatementFuncao.executeUpdate();
                funcaoUpdated = rowsUpdatedFuncao > 0;
            } else {
                // Se não existe, inserir um novo registro
                String sqlInsertFuncao = "INSERT INTO membro_funcoes (usuario_id, projeto_id, funcao) VALUES (?, ?, ?)";
                preparedStatementFuncao = connection.prepareStatement(sqlInsertFuncao);
                preparedStatementFuncao.setInt(1, membro.getId());
                int projetoId = getProjectIdByName(projeto);
                preparedStatementFuncao.setInt(2, projetoId);
                preparedStatementFuncao.setString(3, funcao);
                int rowsInsertedFuncao = preparedStatementFuncao.executeUpdate();
                funcaoUpdated = rowsInsertedFuncao > 0;
            }

            // Confirma a transação se ambas as atualizações foram bem-sucedidas
            if (rowsUpdatedUsuario > 0 && funcaoUpdated) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (preparedStatementUsuario != null) preparedStatementUsuario.close();
                if (preparedStatementFuncao != null) preparedStatementFuncao.close();
                if (preparedStatementCheck != null) preparedStatementCheck.close();
                if (resultSet != null) resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean associarMembroAProjeto(int usuarioId, String projeto, String funcao) {
        try {
            if (isMembroJaAssociadoAoProjeto(usuarioId, projeto)) {
                System.out.println("O membro já está associado a este projeto.");
                return false;
            }

            addMembroFuncao(usuarioId, projeto, funcao);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isMembroJaAssociadoAoProjeto(int usuarioId, String projeto) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM membro_funcoes WHERE usuario_id = ? AND projeto_id = ?";
        int projetoId = getProjectIdByName(projeto);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, projetoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        }
        return false;
    }
}




