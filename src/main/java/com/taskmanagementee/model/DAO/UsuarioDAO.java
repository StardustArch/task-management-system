package com.taskmanagementee.model.DAO;

import com.taskmanagementee.model.DAO.DatabaseConnection;
import com.taskmanagementee.model.Usuario;
//import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    private Connection connection;
    public UsuarioDAO(Connection connection) {

        this.connection = connection;
    }

    public void saveUser(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getEmail());
            pstmt.setString(2, usuario.getSenha());
            pstmt.setString(3, usuario.getPapel());
            pstmt.executeUpdate();
        }
    }
    // Método para buscar um usuário por ID
    public Usuario buscarPorId(int userId) throws SQLException {
        Usuario usuario = null;
        String sql = "SELECT u.id, u.nome, u.email, " +
                "COALESCE(mf.funcao, 'Sem Função') AS funcao, " +
                "u.senha, " +
                "(SELECT COUNT(*) FROM tarefas WHERE responsavel_id = u.id) AS tarefasAtribuidas " +
                "FROM usuarios u " +
                "LEFT JOIN membro_funcoes mf ON u.id = mf.usuario_id " +
                "WHERE u.id = ? AND u.papel != 'gestor'";  // Incluindo o parâmetro na cláusula WHERE
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);  // Definindo o parâmetro correto

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    usuario = new Usuario();
                    usuario.setId(resultSet.getInt("id"));
                    usuario.setNome(resultSet.getString("nome"));
                    usuario.setEmail(resultSet.getString("email"));
                    usuario.setSenha(resultSet.getString("senha"));
                    usuario.setFuncao_membro(resultSet.getString("funcao"));
                    int tarefasAtribuidas = resultSet.getInt("tarefasAtribuidas");
                    usuario.setTarefasAtribuidas(tarefasAtribuidas);
                    String status = tarefasAtribuidas >= 3 ? "Inativo" : "Ativo";
                    usuario.setStatus(status);
                }
            }

        }
        return usuario;
    }


    public Usuario autenticarUsuario(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario novo = new Usuario();
//                if(verificarSenha(senha,rs.getString("senha"))) {
                   novo.setNome(rs.getString("nome"));
                   novo.setEmail(rs.getString("email"));
                   novo.setSenha(rs.getString("senha"));
                    novo.setPapel(rs.getString("papel"));
                    novo.setId(rs.getInt("id"));
//                }
                return novo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Método para atualizar o usuário
    public void atualizarUsuario(Usuario usuario, boolean atualizarSenha) throws SQLException {
        String sql;

        // Se for atualizar a senha, incluímos a senha na consulta SQL
        if (atualizarSenha) {
            sql = "UPDATE usuarios SET nome = ?, email = ?, senha = ? WHERE id = ?";
        } else {
            sql = "UPDATE usuarios SET nome = ?, email = ? WHERE id = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Definimos os parâmetros de nome e email
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());

            // Se for para atualizar a senha, incluímos o valor da senha
            if (atualizarSenha) {
                stmt.setString(3, usuario.getSenha());  // A senha é fornecida diretamente
                stmt.setInt(4, usuario.getId());
            } else {
                stmt.setInt(3, usuario.getId());
            }

            // Executa o comando de atualização
            stmt.executeUpdate();
        }
    }

    // Método para inserir um novo usuário
    public boolean inserirUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, senha, papel) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getPapel());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para verificar se o email já existe
    public boolean verificarEmailExistente(String email) {
        String sql = "SELECT id FROM usuarios WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Se o resultado existir, o email já está cadastrado
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

//    public boolean verificarSenha(String senha, String hash) {
//        // Verifica se a senha fornecida corresponde ao hash armazenado
//        return BCrypt.checkpw(senha, hash);
//    }
}

