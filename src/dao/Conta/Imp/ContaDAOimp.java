package dao.Conta.Imp;

import dao.Conta.ContaDAO;
import model.Conta.Conta;
import utils.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContaDAOimp implements ContaDAO {

    @Override
    public Conta buscarPorNumero(String numeroConta) {
        String sql = "SELECT id_conta, tipo_conta FROM conta WHERE numero_conta = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroConta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idConta = rs.getInt("id_conta");
                    String tipoConta = rs.getString("tipo_conta");

                    // Delega a busca para o DAO específico para obter o objeto completo
                    switch (tipoConta) {
                        case "POUPANCA":
                            // A classe ContaPoupancaDAOimp é instanciada para buscar os detalhes específicos.
                            return new ContaPoupancaDAOimp().buscarPorContaId(idConta);
                        case "CORRENTE":
                            // A classe ContaCorrenteDAOimp é instanciada para buscar os detalhes específicos.
                            return new ContaCorrenteDAOimp().buscarPorContaId(idConta);
                        case "INVESTIMENTO":
                            // A classe ContaInvestimentoDAOimp é instanciada para buscar os detalhes específicos.
                            // Nota: A implementação de ContaInvestimentoDAOimp.buscarPorContaId no seu arquivo original
                            // tem um typo e retorna 'ContaInvestimentoDAO'. O código abaixo funcionará, mas o ideal
                            // é corrigir a assinatura do método em ContaInvestimentoDAOimp para retornar 'ContaInvestimento'.
                            return (Conta) new ContaInvestimentoDAOimp().buscarPorContaId(idConta);
                        default:
                            throw new IllegalStateException("Tipo de conta desconhecido no banco de dados: " + tipoConta);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar conta por número: " + e.getMessage(), e);
        }
        return null; // Retorna null se nenhuma conta for encontrada
    }

    @Override
    public List<Conta> listarPorCliente(int idCliente) {
        List<Conta> contas = new ArrayList<>();
        // Primeiro, busca todos os números de conta daquele cliente.
        String sql = "SELECT numero_conta FROM conta WHERE id_cliente = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Reutiliza o método buscarPorNumero para obter o objeto completo de cada conta.
                    // Isso garante que cada conta venha com seus dados específicos (limite, rendimento, etc.).
                    Conta contaCompleta = buscarPorNumero(rs.getString("numero_conta"));
                    if (contaCompleta != null) {
                        contas.add(contaCompleta);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar contas por cliente: " + e.getMessage(), e);
        }
        return contas;
    }

    @Override
    public void atualizarStatus(int idConta, String novoStatus) {
        String sql = "UPDATE conta SET status = ? WHERE id_conta = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoStatus);
            stmt.setInt(2, idConta);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("Nenhuma conta encontrada com o ID " + idConta + " para atualizar o status.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status da conta: " + e.getMessage(), e);
        }
    }

    @Override
    public void encerrarConta(int idConta, int idFuncionario, String motivo) {
        // Chama a Stored Procedure 'encerrar_conta_cliente' definida no Banco.sql
        String sql = "{CALL encerrar_conta_cliente(?, ?, ?)}";
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, idConta);
            cstmt.setInt(2, idFuncionario);
            cstmt.setString(3, motivo);
            cstmt.execute();
        } catch (SQLException e) {
            // A procedure pode lançar exceções customizadas (com SIGNAL SQLSTATE) que serão capturadas aqui.
            throw new RuntimeException("Erro ao processar o encerramento da conta: " + e.getMessage(), e);
        }
    }
}