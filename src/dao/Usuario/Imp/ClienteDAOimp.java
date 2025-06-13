package dao.Conta.Usuario.Imp;

import dao.Conta.Usuario.ClienteDAO;
import model.Usuario.Cliente;
import utils.ConnectionFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClienteDAOimp implements ClienteDAO {
    @Override
    public void inserir(Cliente cliente) {
        String sqlUsuario = "INSERT INTO usuario (nome, cpf, data_nascimento, telefone, tipo_usuario, senha_hash, otp_ativo, otp_expiracao) VALUES (?, ?, ?, ?, 'CLIENTE', ?, ?, ?)";
        String sqlCliente = "INSERT INTO cliente (id_usuario, score_credito) VALUES (?, ?)";

        // Usamos try-with-resources para garantir que a conexão e os statements sejam fechados.
        try (Connection conn = ConnectionFactory.getConnection()) {
            // Desativa o auto-commit para fazer uma transação
            conn.setAutoCommit(false);

            try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {

                // 1. Inserir na tabela 'usuario'
                stmtUsuario.setString(1, cliente.getNome());
                stmtUsuario.setString(2, cliente.getCpf());
                // Converte a string de data para java.sql.Date
                stmtUsuario.setDate(3, Date.valueOf(LocalDate.parse(cliente.getDataNascimento())));
                stmtUsuario.setString(4, cliente.getTelefone());
                stmtUsuario.setString(5, cliente.getSenhaHash());
                stmtUsuario.setString(6, cliente.getOtpAtivo());
                stmtUsuario.setObject(7, cliente.getOtpExpiracao()); // JDBC 4.2+ pode lidar com LocalDateTime

                stmtUsuario.executeUpdate();

                // Recupera o id_usuario gerado
                long idUsuarioGerado = -1;
                try (ResultSet rs = stmtUsuario.getGeneratedKeys()) {
                    if (rs.next()) {
                        idUsuarioGerado = rs.getLong(1);
                    }
                }

                if (idUsuarioGerado == -1) {
                    conn.rollback(); // Desfaz a transação se não conseguiu o ID
                    throw new SQLException("Falha ao criar usuário, ID não obtido.");
                }

                // 2. Inserir na tabela 'cliente'
                try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente)) {
                    stmtCliente.setLong(1, idUsuarioGerado);
                    stmtCliente.setDouble(2, cliente.getScoreCredito());
                    stmtCliente.executeUpdate();
                }

                // Se tudo deu certo, commita a transação
                conn.commit();

            } catch (SQLException e) {
                // Em caso de erro, desfaz tudo
                conn.rollback();
                System.err.println("Erro na transação ao criar cliente: " + e.getMessage());
                throw e; // Lança a exceção para a camada superior tratar
            }

        } catch (SQLException e) {
            e.printStackTrace();
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
                    // Extrai os dados do ResultSet
                    int idUsuario = rs.getInt("id_usuario");
                    String nome = rs.getString("nome");
                    String cpf = rs.getString("cpf");
                    String dataNascimento = rs.getDate("data_nascimento").toString();
                    String telefone = rs.getString("telefone");
                    String tipoUsuario = rs.getString("tipo_usuario");
                    String senhaHash = rs.getString("senha_hash");
                    String otpAtivo = rs.getString("otp_ativo");
                    LocalDateTime otpExpiracao = rs.getObject("otp_expiracao", LocalDateTime.class);
                    double scoreCredito = rs.getDouble("score_credito");

                    // Cria o objeto Cliente
                    // Note que o construtor original tem um parâmetro 'idUsuario1' redundante.
                    // Ajustei para usar o 'idUsuario' da classe pai corretamente.
                    return new Cliente(idUsuario, nome, cpf, dataNascimento, telefone, tipoUsuario, senhaHash, otpAtivo, otpExpiracao, idCliente, scoreCredito, idUsuario);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retorna null se não encontrar
    }

    @Override
    public Cliente buscarPorUsuarioId(int idUsuario) {
        return null;
    }

    @Override
    public void atualizarScore(int idCliente, double novoScore) {

    }


//    public void criarCliente(Cliente cliente) {
//        SX
//    }
//
//    /**
//     * Busca um cliente pelo seu ID de cliente.
//     * @param idCliente O ID do cliente a ser buscado.
//     * @return um objeto Cliente se encontrado, senão null.
//     */
//    public Cliente buscarClientePorId(int idCliente) {
//        // SQL que junta as tabelas 'cliente' e 'usuario'
//
//
//    /**
//     * Atualiza o score de crédito de um cliente.
//     * Este método implementa a funcionalidade do seu método `setScoreCredito`.
//     * @param idCliente O ID do cliente a ser atualizado.
//     * @param novoScore O novo valor para o score de crédito.
//     * @return true se a atualização foi bem-sucedida, false caso contrário.
//     */
//    public boolean atualizarScoreCredito(int idCliente, double novoScore) {
//        String sql = "UPDATE cliente SET score_credito = ? WHERE id_cliente = ?";
//
//        try (Connection conn = ConnectionFactory.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setDouble(1, novoScore);
//            stmt.setInt(2, idCliente);
//
//            int linhasAfetadas = stmt.executeUpdate();
//            return linhasAfetadas > 0; // Retorna true se pelo menos uma linha foi atualizada
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * Deleta um cliente do banco de dados.
//     * Por causa das chaves estrangeiras, é preciso deletar de 'cliente' primeiro.
//     * ATENÇÃO: Considere desativar um usuário em vez de deletar para manter o histórico.
//     * @param idCliente O ID do cliente a ser deletado.
//     * @return true se a deleção foi bem-sucedida, false caso contrário.
//     */
//    public boolean deletarCliente(int idCliente) {
//        // Primeiro, precisamos do id_usuario para poder deletar da tabela 'usuario'
//        Cliente cliente = buscarClientePorId(idCliente);
//        if (cliente == null) {
//            System.out.println("Cliente não encontrado.");
//            return false;
//        }
//        int idUsuario = cliente.getIdUsuario();
//
//        // SQLs para deletar
//        String sqlCliente = "DELETE FROM cliente WHERE id_cliente = ?";
//        String sqlUsuario = "DELETE FROM usuario WHERE id_usuario = ?";
//
//        // Nota: Você pode ter problemas com chaves estrangeiras em outras tabelas (endereco, conta, etc).
//        // A melhor abordagem é ter uma política de deleção em cascata (ON DELETE CASCADE) no banco,
//        // ou deletar as entradas relacionadas em outras tabelas antes.
//        // Ou, como sugerido, apenas inativar o usuário.
//
//        try (Connection conn = ConnectionFactory.getConnection()) {
//            conn.setAutoCommit(false); // Transação
//
//            try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente);
//                 PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario)) {
//
//                // 1. Deletar da tabela 'cliente'
//                stmtCliente.setInt(1, idCliente);
//                stmtCliente.executeUpdate();
//
//                // 2. Deletar da tabela 'usuario'
//                stmtUsuario.setInt(1, idUsuario);
//                stmtUsuario.executeUpdate();
//
//                conn.commit();
//                return true;
//
//            } catch (SQLException e) {
//                conn.rollback();
//                System.err.println("Erro na transação ao deletar cliente: " + e.getMessage());
//                // Isso pode acontecer se o cliente tiver contas ou endereços associados.
//                return false;
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
}
