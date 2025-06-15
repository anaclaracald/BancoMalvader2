package dao.Outros.Imp;

import dao.Outros.RelatorioDAO;
import model.Relatorio;
import utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class RelatorioDAOimp implements RelatorioDAO {

    @Override
    public void inserir(Relatorio relatorio) {
        String sql = "INSERT INTO relatorio (id_funcionario, tipo_relatorio, data_geracao, conteudo) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, relatorio.getIdFuncionario());
            stmt.setString(2, relatorio.getTipoRelatorio());
            stmt.setTimestamp(3, Timestamp.valueOf(relatorio.getDataGeracao() != null ? relatorio.getDataGeracao() : LocalDateTime.now()));
            stmt.setString(4, relatorio.getConteudo()); // Conteúdo pode ser um JSON, CSV, etc.

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir relatório no banco de dados: " + e.getMessage(), e);
        }
    }
}