package dao.Usuario.Imp;

import dao.Usuario.ClienteDAO;
import model.Usuario.Cliente;
import utils.ConnectionFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOimp implements ClienteDAO {

    @Override
    public void inserir(Cliente cliente) {
        String sqlUsuario = "INSERT INTO usuario (nome, cpf, data_nascimento, telefone, tipo_usuario, senha_hash, otp_ativo, otp_expiracao) VALUES (?, ?, ?, ?, 'CLIENTE', ?, ?, ?)";
        String sqlCliente = "INSERT INTO cliente (id_usuario, score_credito) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false); // Inicia a transação

            long idUsuarioGerado;
            // Etapa 1: Inserir na tabela 'usuario'
            try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                stmtUsuario.setString(1, cliente.getNome());
                stmtUsuario.setString(2, cliente.getCpf());
                stmtUsuario.setDate(3, Date.valueOf(cliente.getDataNascimento())); // Assume formato 'AAAA-MM-DD'
                stmtUsuario.setString(4, cliente.getTelefone());
                stmtUsuario.setString(5, cliente.getSenhaHash());
                stmtUsuario.setString(6, cliente.getOtpAtivo());
                stmtUsuario.setObject(7, cliente.getOtpExpiracao());

                stmtUsuario.executeUpdate();

                // Obter o ID do usuário gerado para usar na tabela 'cliente'
                try (ResultSet rs = stmtUsuario.getGeneratedKeys()) {
                    if (rs.next()) {
                        idUsuarioGerado = rs.getLong(1);
                        cliente.setIdUsuario((int) idUsuarioGerado);
                    } else {
                        conn.rollback();
                        throw new SQLException("Falha ao criar usuário, ID não obtido.");
                    }
                }
            }

            // Etapa 2: Inserir na tabela 'cliente'
            try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente, Statement.RETURN_GENERATED_KEYS)) {
                stmtCliente.setLong(1, idUsuarioGerado);
                stmtCliente.setDouble(2, cliente.getScoreCredito());
                stmtCliente.executeUpdate();

                // Opcional: obter o id_cliente gerado e atualizar o objeto
                try (ResultSet rs = stmtCliente.getGeneratedKeys()) {
                    if(rs.next()){
                        cliente.setIdCliente(rs.getInt(1));
                    }
                }
            }
            conn.commit(); // Efetiva a transação
        } catch (SQLException e) {
            throw new RuntimeException("Erro na transação ao criar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Cliente buscarPorId(int idCliente) {
        String sql = "SELECT u.*, c.id_cliente, c.score_credito FROM cliente c " +
                "JOIN usuario u ON c.id_usuario = u.id_usuario WHERE c.id_cliente = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairClienteDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por ID do cliente: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Cliente buscarPorIdUsuario(int idUsuario) {
        String sql = "SELECT u.*, c.id_cliente, c.score_credito FROM cliente c " +
                "JOIN usuario u ON c.id_usuario = u.id_usuario WHERE u.id_usuario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairClienteDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por ID de usuário: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT u.*, c.id_cliente, c.score_credito FROM cliente c " +
                "JOIN usuario u ON c.id_usuario = u.id_usuario ORDER BY u.nome";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(extrairClienteDoResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todos os clientes: " + e.getMessage(), e);
        }
        return clientes;
    }

    @Override
    public void calcularScoreCredito(int idCliente) {
        String sql = "{CALL calcular_score_credito(?)}"; // Chama a Stored Procedure
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, idCliente);
            cstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao executar procedure para calcular score: " + e.getMessage(), e);
        }
    }

    /**
     * Método auxiliar para não repetir código de mapeamento do ResultSet para o objeto Cliente.
     */
    private Cliente extrairClienteDoResultSet(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_usuario"),
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getDate("data_nascimento").toString(),
                rs.getString("telefone"),
                rs.getString("tipo_usuario"),
                rs.getString("senha_hash"),
                rs.getString("otp_ativo"),
                rs.getObject("otp_expiracao", LocalDateTime.class),
                rs.getInt("id_cliente"),
                rs.getDouble("score_credito")
        );
    }
}