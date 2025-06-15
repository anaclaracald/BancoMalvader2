package dao.Outros.Imp;

import dao.Outros.TransacaoDAO;
import model.TipoTransacao;
import model.Transacao;
import utils.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransacaoDAOimp implements TransacaoDAO {

    @Override
    public void inserir(Transacao transacao) {
        String sql = "INSERT INTO transacao (id_conta_origem, id_conta_destino, tipo_transacao, valor, descricao, data_hora) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Trata campos que podem ser nulos
            if (transacao.getIdContaOrigem() != null) {
                stmt.setInt(1, transacao.getIdContaOrigem());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            if (transacao.getIdContaDestino() != null) {
                stmt.setInt(2, transacao.getIdContaDestino());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, transacao.getTipoTransacao().name());
            stmt.setBigDecimal(4, transacao.getValor());
            stmt.setString(5, transacao.getDescricao());
            stmt.setTimestamp(6, Timestamp.valueOf(transacao.getDataHora() != null ? transacao.getDataHora() : LocalDateTime.now()));

            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()){
                    transacao.setIdTransacao(rs.getInt(1)); // Atualiza o objeto com o ID gerado
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir transação: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transacao> listarPorConta(int idConta, int limite) {
        List<Transacao> transacoes = new ArrayList<>();
        String sql = "SELECT * FROM transacao WHERE id_conta_origem = ? OR id_conta_destino = ? " +
                "ORDER BY data_hora DESC LIMIT ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConta);
            stmt.setInt(2, idConta);
            stmt.setInt(3, limite);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacoes.add(extrairTransacaoDoResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar transações por conta: " + e.getMessage(), e);
        }
        return transacoes;
    }

    @Override
    public List<Transacao> listarPorPeriodo(int idConta, LocalDateTime inicio, LocalDateTime fim) {
        List<Transacao> transacoes = new ArrayList<>();
        String sql = "SELECT * FROM transacao WHERE (id_conta_origem = ? OR id_conta_destino = ?) " +
                "AND data_hora BETWEEN ? AND ? ORDER BY data_hora DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConta);
            stmt.setInt(2, idConta);
            stmt.setTimestamp(3, Timestamp.valueOf(inicio));
            stmt.setTimestamp(4, Timestamp.valueOf(fim));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacoes.add(extrairTransacaoDoResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar transações por período: " + e.getMessage(), e);
        }
        return transacoes;
    }

    /**
     * Método auxiliar para mapear uma linha do ResultSet para um objeto Transacao.
     */
    private Transacao extrairTransacaoDoResultSet(ResultSet rs) throws SQLException {
        return new Transacao(
                rs.getInt("id_transacao"),
                (Integer) rs.getObject("id_conta_origem"),
                (Integer) rs.getObject("id_conta_destino"),
                TipoTransacao.valueOf(rs.getString("tipo_transacao")),
                rs.getBigDecimal("valor"),
                rs.getTimestamp("data_hora").toLocalDateTime(),
                rs.getString("descricao")
        );
    }
}