package dao.Usuario.Imp;

import dao.Usuario.UsuarioDAO;
import model.Usuario.Usuario;
import utils.ConnectionFactory;

import java.sql.*;
import java.time.LocalDateTime;

public class UsuarioDAOimp implements UsuarioDAO {

    /**
     * A inserção de um usuário não é suportada diretamente por esta DAO genérica,
     * pois um usuário deve ser criado como um tipo concreto (Cliente ou Funcionário).
     * A lógica de inserção, que envolve duas tabelas (usuario e cliente/funcionario),
     * deve ser tratada pelas DAOs específicas: {@link ClienteDAOimp} ou {@link FuncionarioDAOimp}.
     *
     * @param usuario O usuário a ser inserido.
     * @throws UnsupportedOperationException Sempre, para reforçar o uso da DAO específica.
     */
    @Override
    public void inserir(Usuario usuario) {
        throw new UnsupportedOperationException("A inserção deve ser feita pela DAO específica (ClienteDAOimp ou FuncionarioDAOimp).");
    }

    /**
     * Busca um usuário pelo seu ID primário (id_usuario).
     * <p>
     * Este método primeiro identifica o tipo de usuário ('CLIENTE' ou 'FUNCIONARIO')
     * e então delega a busca para a DAO correspondente, que retornará o objeto
     * concreto totalmente populado.
     *
     * @param idUsuario O ID do usuário a ser buscado.
     * @return Um objeto {@link Usuario} (podendo ser {@link model.Usuario.Cliente} ou {@link model.Usuario.Funcionario}) se encontrado, caso contrário, null.
     */
    @Override
    public Usuario buscarPorId(int idUsuario) {
        String sql = "SELECT tipo_usuario FROM usuario WHERE id_usuario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tipoUsuario = rs.getString("tipo_usuario");
                    // Delega para a DAO específica baseada no tipo
                    if ("CLIENTE".equals(tipoUsuario)) {
                        return new ClienteDAOimp().buscarPorIdUsuario(idUsuario);
                    } else if ("FUNCIONARIO".equals(tipoUsuario)) {
                        // Assume-se que FuncionarioDAOimp.buscarPorIdUsuario está implementado
                        return new FuncionarioDAOimp().buscarPorIdUsuario(idUsuario);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por ID: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Busca um usuário pelo seu CPF, que é um campo único.
     * <p>
     * A lógica é similar à busca por ID: descobre o tipo e delega para a DAO específica.
     *
     * @param cpf O CPF do usuário a ser buscado.
     * @return Um objeto {@link Usuario} (concreto) se encontrado, caso contrário, null.
     */
    @Override
    public Usuario buscarPorCpf(String cpf) {
        String sql = "SELECT id_usuario, tipo_usuario FROM usuario WHERE cpf = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idUsuario = rs.getInt("id_usuario");
                    String tipoUsuario = rs.getString("tipo_usuario");
                    if ("CLIENTE".equals(tipoUsuario)) {
                        return new ClienteDAOimp().buscarPorIdUsuario(idUsuario);
                    } else if ("FUNCIONARIO".equals(tipoUsuario)) {
                        return new FuncionarioDAOimp().buscarPorIdUsuario(idUsuario);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por CPF: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * A atualização de um usuário não é suportada diretamente por esta DAO genérica.
     * A lógica de atualização deve ser tratada pelas DAOs específicas para garantir
     * que todos os campos (da tabela 'usuario' e da tabela 'cliente'/'funcionario')
     * sejam atualizados corretamente.
     *
     * @param usuario O usuário com os dados a serem atualizados.
     * @throws UnsupportedOperationException Sempre, para reforçar o uso da DAO específica.
     */
    @Override
    public void atualizar(Usuario usuario) {
        throw new UnsupportedOperationException("A atualização deve ser feita pela DAO específica (ClienteDAOimp ou FuncionarioDAOimp).");
    }

    /**
     * Verifica as credenciais de um usuário (CPF e senha) para autenticação.
     *
     * @param cpf       O CPF fornecido no login.
     * @param senhaHash A senha já convertida para Hash MD5 para comparação no banco.
     * @return O objeto {@link Usuario} completo se as credenciais forem válidas, caso contrário, null.
     */
    @Override
    public Usuario verificarCredenciais(String cpf, String senhaHash) {
        // A busca por CPF já retorna o objeto de usuário completo, então podemos reutilizá-la.
        Usuario usuario = buscarPorCpf(cpf);
        if (usuario != null && usuario.getSenhaHash().equals(senhaHash)) {
            return usuario;
        }
        return null;
    }

    /**
     * Atualiza o código OTP (One-Time Password) e sua data de expiração para um usuário.
     *
     * @param idUsuario O ID do usuário cujo OTP será atualizado.
     * @param otp       O novo código OTP de 6 dígitos.
     * @param expiracao A data e hora em que o novo OTP irá expirar.
     */
    @Override
    public void atualizarOtp(int idUsuario, String otp, LocalDateTime expiracao) {
        String sql = "UPDATE usuario SET otp_ativo = ?, otp_expiracao = ? WHERE id_usuario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, otp);
            stmt.setTimestamp(2, Timestamp.valueOf(expiracao));
            stmt.setInt(3, idUsuario);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar OTP do usuário: " + e.getMessage(), e);
        }
    }

    /**
     * Busca os dados de OTP de um usuário.
     * <p>
     * Embora o objetivo seja apenas validar o OTP, a interface exige o retorno do
     * objeto Usuario completo. O método reutiliza a lógica de busca por ID.
     *
     * @param idUsuario O ID do usuário.
     * @return O objeto {@link Usuario} completo, contendo os campos de OTP.
     */
    @Override
    public Usuario buscarOtp(int idUsuario) {
        // A busca por ID já retorna o objeto completo, que inclui os campos de OTP.
        return buscarPorId(idUsuario);
    }

    /**
     * Chama a Stored Procedure do banco de dados para atualizar a senha de um usuário.
     * A procedure contém a lógica de validação de força da senha.
     *
     * @param idUsuario O ID do usuário.
     * @param novaSenha A nova senha em texto plano (a procedure fará o hash).
     */
    @Override
    public void atualizarSenha(int idUsuario, String novaSenha) {
        String sql = "{CALL atualizar_senha_usuario(?, ?)}";
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, idUsuario);
            cstmt.setString(2, novaSenha);
            cstmt.execute();
        } catch (SQLException e) {
            // A procedure pode lançar uma exceção customizada se a senha for fraca.
            throw new RuntimeException("Erro ao processar a atualização de senha: " + e.getMessage(), e);
        }
    }

    /**
     * Chama a Stored Procedure para registrar uma tentativa de login (bem-sucedida ou falha)
     * na tabela de auditoria.
     *
     * @param cpf            O CPF ou nome de usuário usado na tentativa.
     * @param sucesso        {@code true} se o login foi bem-sucedido, {@code false} caso contrário.
     * @param infoAdicional  Informações extras (ex: endereço IP, User-Agent) para auditoria.
     */
    @Override
    public void registrarTentativaLogin(String cpf, boolean sucesso, String infoAdicional) {
        String sql = "{CALL registrar_tentativa_login(?, ?, ?)}";
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, cpf);
            cstmt.setBoolean(2, sucesso);
            cstmt.setString(3, infoAdicional);
            cstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar tentativa de login: " + e.getMessage(), e);
        }
    }
}