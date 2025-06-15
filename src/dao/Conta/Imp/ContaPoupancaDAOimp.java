package dao.Conta.Imp;

import dao.Conta.ContaPoupancaDAO;
import model.Conta.ContaPoupanca;
import utils.ConnectionFactory;

import java.sql.*;
import java.time.LocalDateTime;

public class ContaPoupancaDAOimp implements ContaPoupancaDAO {

    @Override
    public void inserir(ContaPoupanca contaPoupanca) {
        // SQL para a tabela genérica 'conta'
        String sqlConta = "INSERT INTO conta (id_cliente, id_agencia, numero_conta, saldo, data_abertura, status, tipo_conta, id_funcionario_abertura) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'POUPANCA', ?)";
        // SQL para a tabela específica 'conta_poupanca'
        String sqlPoupanca = "INSERT INTO conta_poupanca (id_conta, taxa_rendimento, ultimo_rendimento) VALUES (?, ?, ?)";

        // O 'try-with-resources' garante que a conexão seja fechada ao final do bloco
        try (Connection conn = ConnectionFactory.getConnection()) {
            // Inicia a transação para garantir a atomicidade da operação
            conn.setAutoCommit(false);

            long idContaGerado;

            // 1. Inserir na tabela 'conta'
            try (PreparedStatement stmtConta = conn.prepareStatement(sqlConta, Statement.RETURN_GENERATED_KEYS)) {
                stmtConta.setInt(1, contaPoupanca.getIdCliente());
                stmtConta.setInt(2, contaPoupanca.getIdAgencia());
                stmtConta.setString(3, contaPoupanca.getNumeroConta());
                stmtConta.setBigDecimal(4, contaPoupanca.getSaldo());
                stmtConta.setTimestamp(5, Timestamp.valueOf(contaPoupanca.getDataAbertura() != null ? contaPoupanca.getDataAbertura() : LocalDateTime.now()));
                stmtConta.setString(6, contaPoupanca.getStatus());

                // Adicionado: tratamento para o funcionário que abriu a conta (pode ser nulo)
                if (contaPoupanca.getIdFuncionarioAbertura() != null) {
                    stmtConta.setInt(7, contaPoupanca.getIdFuncionarioAbertura());
                } else {
                    stmtConta.setNull(7, Types.INTEGER);
                }
                stmtConta.executeUpdate();

                // Recupera o ID gerado pela inserção na tabela 'conta'
                try (ResultSet rs = stmtConta.getGeneratedKeys()) {
                    if (rs.next()) {
                        idContaGerado = rs.getLong(1);
                        contaPoupanca.setIdConta((int) idContaGerado);
                    } else {
                        throw new SQLException("Falha ao obter o ID da conta, nenhuma linha afetada.");
                    }
                }
            }

            // 2. Inserir na tabela 'conta_poupanca' usando o ID gerado
            try (PreparedStatement stmtPoupanca = conn.prepareStatement(sqlPoupanca)) {
                stmtPoupanca.setLong(1, idContaGerado);
                stmtPoupanca.setDouble(2, contaPoupanca.getTaxaRendimento());
                // ultimo_rendimento pode ser nulo na criação da conta
                if (contaPoupanca.getUltimoRendimento() != null) {
                    stmtPoupanca.setTimestamp(3, Timestamp.valueOf(contaPoupanca.getUltimoRendimento()));
                } else {
                    stmtPoupanca.setNull(3, Types.TIMESTAMP);
                }
                stmtPoupanca.executeUpdate();
            }

            // Se ambas as inserções ocorrerem sem erros, efetiva a transação
            conn.commit();

        } catch (SQLException e) {
            // Se ocorrer um erro, a transação não é commitada e o 'try-with-resources'
            // fecha a conexão, desfazendo as alterações pendentes (rollback).
            throw new RuntimeException("Erro ao inserir conta poupança: " + e.getMessage(), e);
        }
    }

    @Override
    public ContaPoupanca buscarPorContaId(int idConta) {
        String sql = "SELECT c.*, cp.taxa_rendimento, cp.ultimo_rendimento " +
                "FROM conta c JOIN conta_poupanca cp ON c.id_conta = cp.id_conta " +
                "WHERE c.id_conta = ? AND c.tipo_conta = 'POUPANCA'";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Mapeia os dados do ResultSet para o objeto ContaPoupanca
                    ContaPoupanca conta = new ContaPoupanca(
                            rs.getInt("id_conta"),
                            rs.getString("numero_conta"),
                            rs.getBigDecimal("saldo"),
                            rs.getString("tipo_conta"),
                            rs.getTimestamp("data_abertura").toLocalDateTime(),
                            rs.getString("status"),
                            rs.getInt("id_agencia"),
                            rs.getInt("id_cliente"),
                            rs.getDouble("taxa_rendimento"),
                            // Trata o campo de data/hora que pode ser nulo
                            rs.getTimestamp("ultimo_rendimento") != null ? rs.getTimestamp("ultimo_rendimento").toLocalDateTime() : null
                    );
                    // Adicionado: Popula o campo opcional da classe pai 'Conta'
                    conta.setIdFuncionarioAbertura((Integer) rs.getObject("id_funcionario_abertura"));
                    return conta;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar conta poupança: " + e.getMessage(), e);
        }
        return null; // Retorna null se não encontrar
    }

    @Override
    public void atualizarRendimento(int idConta, double novoValor) {
        // Este método, conforme a implementação original, atualiza a TAXA de rendimento.
        // Ele não aplica o rendimento ao saldo da conta.
        String sql = "UPDATE conta_poupanca SET taxa_rendimento = ? WHERE id_conta = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, novoValor);
            stmt.setInt(2, idConta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar a taxa de rendimento: " + e.getMessage(), e);
        }
    }
}