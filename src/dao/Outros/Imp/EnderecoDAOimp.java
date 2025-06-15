package dao.Outros.Imp;

import dao.Outros.EnderecoDAO;
import model.Endereco;
import utils.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnderecoDAOimp implements EnderecoDAO {

    @Override
    public void inserir(Endereco endereco) {
        String sql = "INSERT INTO endereco (id_usuario, cep, local, numero_casa, bairro, cidade, estado, complemento) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, endereco.getIdUsuario());
            stmt.setString(2, endereco.getCep());
            stmt.setString(3, endereco.getLocal());
            stmt.setInt(4, endereco.getNumeroCasa());
            stmt.setString(5, endereco.getBairro());
            stmt.setString(6, endereco.getCidade());
            stmt.setString(7, endereco.getEstado());
            stmt.setString(8, endereco.getComplemento());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    endereco.setIdEndereco(rs.getInt(1)); // Atualiza o objeto com o ID gerado
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir endereço: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Endereco> buscarPorIdUsuario(int idUsuario) {
        List<Endereco> enderecos = new ArrayList<>();
        String sql = "SELECT * FROM endereco WHERE id_usuario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enderecos.add(extrairEnderecoDoResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar endereços por ID de usuário: " + e.getMessage(), e);
        }
        return enderecos;
    }

    @Override
    public void atualizar(Endereco endereco) {
        String sql = "UPDATE endereco SET id_usuario = ?, cep = ?, local = ?, numero_casa = ?, " +
                "bairro = ?, cidade = ?, estado = ?, complemento = ? WHERE id_endereco = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, endereco.getIdUsuario());
            stmt.setString(2, endereco.getCep());
            stmt.setString(3, endereco.getLocal());
            stmt.setInt(4, endereco.getNumeroCasa());
            stmt.setString(5, endereco.getBairro());
            stmt.setString(6, endereco.getCidade());
            stmt.setString(7, endereco.getEstado());
            stmt.setString(8, endereco.getComplemento());
            stmt.setInt(9, endereco.getIdEndereco());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar endereço: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(int idEndereco) {
        String sql = "DELETE FROM endereco WHERE id_endereco = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEndereco);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir endereço: " + e.getMessage(), e);
        }
    }

    /**
     * Método auxiliar para mapear uma linha do ResultSet para um objeto Endereco.
     */
    private Endereco extrairEnderecoDoResultSet(ResultSet rs) throws SQLException {
        return new Endereco(
                rs.getInt("id_endereco"),
                rs.getInt("id_usuario"),
                rs.getString("cep"),
                rs.getString("local"),
                rs.getInt("numero_casa"),
                rs.getString("bairro"),
                rs.getString("cidade"),
                rs.getString("estado"),
                rs.getString("complemento")
        );
    }
}