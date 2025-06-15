package dao.Usuario.Imp;

import dao.Usuario.FuncionarioDAO;
import model.Usuario.Cargo;
import model.Usuario.Funcionario;
import utils.ConnectionFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAOimp implements FuncionarioDAO {

    @Override
    public void inserir(Funcionario funcionario) {
        String sqlUsuario = "INSERT INTO usuario (nome, cpf, data_nascimento, telefone, tipo_usuario, senha_hash, otp_ativo, otp_expiracao) VALUES (?, ?, ?, ?, 'FUNCIONARIO', ?, ?, ?)";
        String sqlFuncionario = "INSERT INTO funcionario (id_usuario, codigo_funcionario, cargo, id_supervisor, id_agencia) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false); // Inicia a transação

            long idUsuarioGerado;
            // Etapa 1: Inserir na tabela 'usuario'
            try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                stmtUsuario.setString(1, funcionario.getNome());
                stmtUsuario.setString(2, funcionario.getCpf());
                stmtUsuario.setDate(3, Date.valueOf(funcionario.getDataNascimento()));
                stmtUsuario.setString(4, funcionario.getTelefone());
                stmtUsuario.setString(5, funcionario.getSenhaHash());
                stmtUsuario.setString(6, funcionario.getOtpAtivo());
                stmtUsuario.setObject(7, funcionario.getOtpExpiracao());
                stmtUsuario.executeUpdate();

                // Obter o ID do usuário gerado para usar na tabela 'funcionario'
                try (ResultSet rs = stmtUsuario.getGeneratedKeys()) {
                    if (rs.next()) {
                        idUsuarioGerado = rs.getLong(1);
                        funcionario.setIdUsuario((int) idUsuarioGerado);
                    } else {
                        conn.rollback(); // Desfaz a transação se não conseguiu o ID
                        throw new SQLException("Falha ao criar usuário para o funcionário, ID não obtido.");
                    }
                }
            }

            // Etapa 2: Inserir na tabela 'funcionario'
            try (PreparedStatement stmtFunc = conn.prepareStatement(sqlFuncionario, Statement.RETURN_GENERATED_KEYS)) {
                stmtFunc.setLong(1, idUsuarioGerado);
                stmtFunc.setString(2, funcionario.getCodigoFuncionario());
                stmtFunc.setString(3, funcionario.getCargo().name());

                stmtFunc.setObject(4, funcionario.getIdSupervisor());
                stmtFunc.setObject(5, funcionario.getIdAgencia());

                stmtFunc.executeUpdate();
                try (ResultSet rs = stmtFunc.getGeneratedKeys()){
                    if(rs.next()){
                        funcionario.setIdFuncionario(rs.getInt(1));
                    }
                }
            }
            conn.commit(); // Confirma a transação se tudo deu certo
        } catch (SQLException e) {
            throw new RuntimeException("Erro na transação ao inserir funcionário: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Funcionario funcionario) {
        String sqlUsuario = "UPDATE usuario SET nome = ?, cpf = ?, data_nascimento = ?, telefone = ? WHERE id_usuario = ?";
        String sqlFuncionario = "UPDATE funcionario SET codigo_funcionario = ?, cargo = ?, id_supervisor = ?, id_agencia = ? WHERE id_funcionario = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            // Etapa 1: Atualizar a tabela 'usuario'
            try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario)) {
                stmtUsuario.setString(1, funcionario.getNome());
                stmtUsuario.setString(2, funcionario.getCpf());
                stmtUsuario.setDate(3, Date.valueOf(funcionario.getDataNascimento()));
                stmtUsuario.setString(4, funcionario.getTelefone());
                stmtUsuario.setInt(5, funcionario.getIdUsuario());
                stmtUsuario.executeUpdate();
            }

            // Etapa 2: Atualizar a tabela 'funcionario'
            try (PreparedStatement stmtFunc = conn.prepareStatement(sqlFuncionario)) {
                stmtFunc.setString(1, funcionario.getCodigoFuncionario());
                stmtFunc.setString(2, funcionario.getCargo().name());
                stmtFunc.setObject(3, funcionario.getIdSupervisor());
                stmtFunc.setObject(4, funcionario.getIdAgencia());
                stmtFunc.setInt(5, funcionario.getIdFuncionario());
                stmtFunc.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Erro na transação ao atualizar funcionário: " + e.getMessage(), e);
        }
    }

    @Override
    public Funcionario buscarPorId(int idFuncionario) {
        String sql = "SELECT u.*, f.id_funcionario, f.codigo_funcionario, f.cargo, f.id_supervisor, f.id_agencia " +
                "FROM funcionario f JOIN usuario u ON f.id_usuario = u.id_usuario " +
                "WHERE f.id_funcionario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFuncionario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairFuncionarioDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar funcionário por ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Funcionario buscarPorCodigo(String codigoFuncionario) {
        String sql = "SELECT u.*, f.id_funcionario, f.codigo_funcionario, f.cargo, f.id_supervisor, f.id_agencia " +
                "FROM funcionario f JOIN usuario u ON f.id_usuario = u.id_usuario " +
                "WHERE f.codigo_funcionario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigoFuncionario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairFuncionarioDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar funcionário por código: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Funcionario buscarPorIdUsuario(int idUsuario) {
        String sql = "SELECT u.*, f.id_funcionario, f.codigo_funcionario, f.cargo, f.id_supervisor, f.id_agencia " +
                "FROM funcionario f JOIN usuario u ON f.id_usuario = u.id_usuario " +
                "WHERE u.id_usuario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairFuncionarioDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar funcionário por ID de usuário: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Funcionario> listarTodos() {
        List<Funcionario> funcionarios = new ArrayList<>();
        String sql = "SELECT u.*, f.id_funcionario, f.codigo_funcionario, f.cargo, f.id_supervisor, f.id_agencia " +
                "FROM funcionario f JOIN usuario u ON f.id_usuario = u.id_usuario ORDER BY u.nome";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                funcionarios.add(extrairFuncionarioDoResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todos os funcionários: " + e.getMessage(), e);
        }
        return funcionarios;
    }

    private Funcionario extrairFuncionarioDoResultSet(ResultSet rs) throws SQLException {
        return new Funcionario(
                rs.getInt("id_usuario"),
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getDate("data_nascimento").toString(),
                rs.getString("telefone"),
                rs.getString("tipo_usuario"),
                rs.getString("senha_hash"),
                rs.getString("otp_ativo"),
                rs.getObject("otp_expiracao", LocalDateTime.class),
                rs.getInt("id_funcionario"),
                rs.getString("codigo_funcionario"),
                Cargo.valueOf(rs.getString("cargo")),
                (Integer) rs.getObject("id_supervisor"),
                (Integer) rs.getObject("id_agencia")
        );
    }
}