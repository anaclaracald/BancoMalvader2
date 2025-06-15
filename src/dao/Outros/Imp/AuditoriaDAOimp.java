package dao.Outros.Imp;

import dao.Outros.AuditoriaDAO;
import model.Auditoria;
import utils.ConnectionFactory;

import java.sql.*;
import java.time.LocalDateTime;

public class AuditoriaDAOimp implements AuditoriaDAO {

    @Override
    public void inserir(Auditoria auditoria) {
        String sql = "INSERT INTO auditoria (id_usuario, acao, data_hora, detalhes) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // O ID do usuário pode ser nulo se a ação for do próprio sistema
            if (auditoria.getIdUsuario() != 0) { // O tipo primitivo int não pode ser nulo, 0 é usado como sentinela
                stmt.setInt(1, auditoria.getIdUsuario());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            stmt.setString(2, auditoria.getAcao());
            // A data_hora pode ser gerada pelo banco, mas permitimos que a aplicação a defina
            stmt.setTimestamp(3, Timestamp.valueOf(auditoria.getDataHora() != null ? auditoria.getDataHora() : LocalDateTime.now()));
            stmt.setString(4, auditoria.getDetalhes());

            stmt.executeUpdate();

        } catch (SQLException e) {
            // Em geral, falhas ao auditar são críticas, mas aqui apenas lançamos a exceção.
            // Em um sistema de produção, poderia haver um mecanismo de fallback (log em arquivo).
            throw new RuntimeException("Erro ao inserir registro de auditoria: " + e.getMessage(), e);
        }
    }
}