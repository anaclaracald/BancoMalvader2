package dao.Outros.Imp;

import dao.Outros.AgenciaDAO;
import model.Agencia;
import utils.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgenciaDAOimp implements AgenciaDAO {

    @Override
    public void inserir(Agencia agencia) {
        String sql = "INSERT INTO agencia (nome, codigo_agencia, endereco_id) VALUES (?, ?, ?)";
        // Usando try-with-resources para garantir que a conexão e o PreparedStatement sejam fechados
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, agencia.getNome());
            stmt.setString(2, agencia.getCodigoAgencia());
            stmt.setInt(3, agencia.getEnderecoId());
            stmt.executeUpdate();

            // Opcional: atualiza o objeto 'agencia' com o ID gerado pelo banco
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    agencia.setIdAgencia(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            // Lança uma exceção de tempo de execução para sinalizar falha na camada de acesso a dados
            throw new RuntimeException("Erro ao inserir agência: " + e.getMessage(), e);
        }
    }

    @Override
    public Agencia buscarPorId(int idAgencia) {
        String sql = "SELECT * FROM agencia WHERE id_agencia = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAgencia);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairAgenciaDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agência por ID: " + e.getMessage(), e);
        }
        return null; // Retorna null se não encontrar
    }

    @Override
    public Agencia buscarPorCodigo(String codigoAgencia) {
        String sql = "SELECT * FROM agencia WHERE codigo_agencia = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoAgencia);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairAgenciaDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agência por código: " + e.getMessage(), e);
        }
        return null; // Retorna null se não encontrar
    }

    @Override
    public List<Agencia> listarTodas() {
        List<Agencia> agencias = new ArrayList<>();
        String sql = "SELECT * FROM agencia ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                agencias.add(extrairAgenciaDoResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todas as agências: " + e.getMessage(), e);
        }
        return agencias;
    }


    private Agencia extrairAgenciaDoResultSet(ResultSet rs) throws SQLException {
        return new Agencia(
                rs.getInt("id_agencia"),
                rs.getString("nome"),
                rs.getString("codigo_agencia"),
                rs.getInt("endereco_id")
        );
    }
}