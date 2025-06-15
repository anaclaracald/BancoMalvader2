package dao.Conta.Imp;

import dao.Conta.ContaInvestimentoDAO;
import model.Conta.ContaInvestimento;
import utils.ConnectionFactory;

import java.sql.*;
import java.time.LocalDateTime;

public class ContaInvestimentoDAOimp implements ContaInvestimentoDAO {

    @Override
    public void inserir(ContaInvestimento conta) {
        String sqlConta = "INSERT INTO conta (id_cliente, id_agencia, numero_conta, saldo, data_abertura, status, tipo_conta, id_funcionario_abertura) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'INVESTIMENTO', ?)";
        String sqlContaInvestimento = "INSERT INTO conta_investimento (id_conta, perfil_risco, valor_minimo, taxa_rendimento_base) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false); // Inicia a transação

            long idContaGerado;

            // 1. Inserir na tabela 'conta'
            try (PreparedStatement stmtConta = conn.prepareStatement(sqlConta, Statement.RETURN_GENERATED_KEYS)) {
                stmtConta.setInt(1, conta.getIdCliente());
                stmtConta.setInt(2, conta.getIdAgencia());
                stmtConta.setString(3, conta.getNumeroConta());
                stmtConta.setBigDecimal(4, conta.getSaldo());
                stmtConta.setTimestamp(5, Timestamp.valueOf(conta.getDataAbertura() != null ? conta.getDataAbertura() : LocalDateTime.now()));
                stmtConta.setString(6, conta.getStatus());

                if (conta.getIdFuncionarioAbertura() != null) {
                    stmtConta.setInt(7, conta.getIdFuncionarioAbertura());
                } else {
                    stmtConta.setNull(7, Types.INTEGER);
                }

                int affectedRows = stmtConta.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Falha ao criar conta, nenhuma linha afetada.");
                }

                try (ResultSet generatedKeys = stmtConta.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idContaGerado = generatedKeys.getLong(1);
                        conta.setIdConta((int) idContaGerado); // Atualiza o objeto com o ID gerado
                    } else {
                        throw new SQLException("Falha ao criar conta, nenhum ID obtido.");
                    }
                }
            }

            // 2. Inserir na tabela 'conta_investimento'
            try (PreparedStatement stmtInvestimento = conn.prepareStatement(sqlContaInvestimento)) {
                stmtInvestimento.setLong(1, idContaGerado);
                // Corrigido: Usando o getPerfilRisco() diretamente, pois é uma String.
                // O ENUM 'perfil_risco' no banco aceitará a string 'BAIXO', 'MEDIO', ou 'ALTO'.
                stmtInvestimento.setString(2, conta.getPerfilRisco());
                stmtInvestimento.setBigDecimal(3, conta.getValorMinimo());
                stmtInvestimento.setDouble(4, conta.getTaxaRendimentoBase());
                stmtInvestimento.executeUpdate();
            }

            conn.commit(); // Efetiva a transação

        } catch (SQLException e) {
            // Lançando RuntimeException para consistência com as outras DAOs implementadas.
            throw new RuntimeException("Erro ao inserir conta de investimento: " + e.getMessage(), e);
        }
    }


    @Override
    public ContaInvestimento buscarPorContaId(int idConta) {
        String sql = "SELECT c.id_conta, c.numero_conta, c.id_agencia, c.saldo, c.tipo_conta, " +
                "c.id_cliente, c.id_funcionario_abertura, c.data_abertura, c.status, " +
                "ci.perfil_risco, ci.valor_minimo, ci.taxa_rendimento_base " +
                "FROM conta c " +
                "JOIN conta_investimento ci ON c.id_conta = ci.id_conta " +
                "WHERE c.id_conta = ? AND c.tipo_conta = 'INVESTIMENTO'";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Mapeia o resultado para o objeto
                    ContaInvestimento conta = new ContaInvestimento(
                            rs.getInt("id_conta"),
                            rs.getString("numero_conta"),
                            rs.getBigDecimal("saldo"),
                            rs.getString("tipo_conta"),
                            rs.getTimestamp("data_abertura").toLocalDateTime(),
                            rs.getString("status"),
                            rs.getInt("id_agencia"),
                            rs.getInt("id_cliente"),
                            rs.getString("perfil_risco"), // O valor vem como String do banco
                            rs.getBigDecimal("valor_minimo"),
                            rs.getDouble("taxa_rendimento_base")
                    );

                    // Corrigido: O método setter correto é setIdFuncionarioAbertura.
                    // O cast para Integer é necessário pois getObject pode retornar null.
                    conta.setIdFuncionarioAbertura((Integer) rs.getObject("id_funcionario_abertura"));

                    return conta;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar conta de investimento por ID: " + e.getMessage(), e);
        }
        return null; // Retorna null se a conta não for encontrada
    }
}