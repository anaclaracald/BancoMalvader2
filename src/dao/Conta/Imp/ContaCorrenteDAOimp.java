package dao.Conta.Imp;

import dao.Conta.ContaCorrenteDAO;
import model.Conta.ContaCorrente;
import utils.ConnectionFactory;

import java.sql.*;
import java.time.LocalDateTime;

public class ContaCorrenteDAOimp implements ContaCorrenteDAO {

    @Override
    public void inserir(ContaCorrente contaCorrente) {
        // SQL para inserir na tabela 'conta' genérica
        String sqlConta = "INSERT INTO conta (id_cliente, id_agencia, numero_conta, saldo, data_abertura, status, tipo_conta, id_funcionario_abertura) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'CORRENTE', ?)";
        // SQL para inserir os dados específicos na tabela 'conta_corrente'
        String sqlCorrente = "INSERT INTO conta_corrente (id_conta, limite, data_vencimento, taxa_manutencao) VALUES (?, ?, ?, ?)";

        // A transação garante que a operação só seja concluída se ambas as inserções funcionarem.
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false); // Inicia a transação

            long idContaGerado;

            // 1. Inserir na tabela 'conta'
            try (PreparedStatement stmtConta = conn.prepareStatement(sqlConta, Statement.RETURN_GENERATED_KEYS)) {
                stmtConta.setInt(1, contaCorrente.getIdCliente());
                stmtConta.setInt(2, contaCorrente.getIdAgencia());
                stmtConta.setString(3, contaCorrente.getNumeroConta());
                stmtConta.setBigDecimal(4, contaCorrente.getSaldo());
                stmtConta.setTimestamp(5, Timestamp.valueOf(contaCorrente.getDataAbertura() != null ? contaCorrente.getDataAbertura() : LocalDateTime.now()));
                stmtConta.setString(6, contaCorrente.getStatus());

                if (contaCorrente.getIdFuncionarioAbertura() != null) {
                    stmtConta.setInt(7, contaCorrente.getIdFuncionarioAbertura());
                } else {
                    stmtConta.setNull(7, Types.INTEGER);
                }

                stmtConta.executeUpdate();

                // Recupera o ID gerado para a conta
                try (ResultSet rs = stmtConta.getGeneratedKeys()) {
                    if (rs.next()) {
                        idContaGerado = rs.getLong(1);
                        contaCorrente.setIdConta((int) idContaGerado); // Atualiza o objeto com o novo ID
                    } else {
                        conn.rollback();
                        throw new SQLException("Falha ao criar conta, nenhum ID obtido.");
                    }
                }
            }

            // 2. Inserir na tabela 'conta_corrente'
            try (PreparedStatement stmtCorrente = conn.prepareStatement(sqlCorrente)) {
                stmtCorrente.setLong(1, idContaGerado);
                stmtCorrente.setDouble(2, contaCorrente.getLimite());
                stmtCorrente.setDate(3, Date.valueOf(contaCorrente.getDataVencimento()));
                stmtCorrente.setDouble(4, contaCorrente.getTaxaManutencao());
                stmtCorrente.executeUpdate();
            }

            conn.commit(); // Confirma a transação

        } catch (SQLException e) {
            // Em uma aplicação real, seria bom registrar o erro (log).
            // A conexão será revertida (rollback) automaticamente se o commit falhar
            // ou se uma exceção for lançada antes dele.
            throw new RuntimeException("Erro ao inserir conta corrente: " + e.getMessage(), e);
        }
    }

    @Override
    public ContaCorrente buscarPorContaId(int idConta) {
        String sql = "SELECT c.*, cc.limite, cc.data_vencimento, cc.taxa_manutencao " +
                "FROM conta c JOIN conta_corrente cc ON c.id_conta = cc.id_conta " +
                "WHERE c.id_conta = ? AND c.tipo_conta = 'CORRENTE'";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Mapeia o ResultSet para o objeto ContaCorrente
                    ContaCorrente conta = new ContaCorrente(
                            rs.getInt("id_conta"),
                            rs.getString("numero_conta"),
                            rs.getBigDecimal("saldo"),
                            rs.getString("tipo_conta"),
                            rs.getTimestamp("data_abertura").toLocalDateTime(),
                            rs.getString("status"),
                            rs.getInt("id_agencia"),
                            rs.getInt("id_cliente"),
                            rs.getDouble("limite"),
                            rs.getDate("data_vencimento").toLocalDate(),
                            rs.getDouble("taxa_manutencao")
                    );
                    // Popula o campo opcional da classe pai
                    conta.setIdFuncionarioAbertura((Integer) rs.getObject("id_funcionario_abertura"));
                    return conta;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar conta corrente por ID: " + e.getMessage(), e);
        }
        return null; // Retorna null se não encontrar
    }

    @Override
    public void atualizarLimite(int idConta, double novoLimite) {
        String sql = "UPDATE conta_corrente SET limite = ? WHERE id_conta = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, novoLimite);
            stmt.setInt(2, idConta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar limite da conta corrente: " + e.getMessage(), e);
        }
    }
}