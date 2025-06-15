package dao.Usuario;

import model.Usuario.Usuario;

import java.time.LocalDateTime;

/**
 * Interface que define as operações de acesso a dados para a entidade {@link Usuario}.
 * <p>
 * Esta DAO atua como um ponto de entrada genérico para operações de usuário,
 * frequentemente delegando a responsabilidade para DAOs mais específicas
 * como {@link ClienteDAO} e {@link FuncionarioDAO} quando necessário.
 */
public interface UsuarioDAO {

    /**
     * Define o contrato para inserir um novo usuário.
     * A implementação em {@link Imp.UsuarioDAOimp} lança {@link UnsupportedOperationException},
     * pois a inserção deve ser feita por DAOs específicas (Cliente ou Funcionário)
     * para garantir a consistência transacional entre as tabelas 'usuario' e 'cliente'/'funcionario'.
     *
     * @param usuario O objeto usuário a ser inserido.
     */
    void inserir(Usuario usuario);

    /**
     * Busca um usuário genérico pelo seu ID.
     * A implementação deve ser capaz de retornar o tipo concreto (Cliente ou Funcionário).
     *
     * @param idUsuario O ID do usuário a ser buscado.
     * @return O objeto Usuario correspondente ou null se não for encontrado.
     */
    Usuario buscarPorId(int idUsuario);

    /**
     * Busca um usuário genérico pelo seu CPF.
     *
     * @param cpf O CPF do usuário a ser buscado.
     * @return O objeto Usuario correspondente ou null se não for encontrado.
     */
    Usuario buscarPorCpf(String cpf);

    /**
     * Define o contrato para atualizar os dados de um usuário.
     * Assim como na inserção, a implementação em {@link Imp.UsuarioDAOimp} não suporta esta operação,
     * delegando-a para as DAOs específicas.
     *
     * @param usuario O objeto usuário com os dados atualizados.
     */
    void atualizar(Usuario usuario);

    /**
     * Verifica se as credenciais (CPF e senha com hash) de um usuário são válidas.
     *
     * @param cpf       O CPF do usuário.
     * @param senhaHash A senha já codificada em hash para comparação.
     * @return O objeto Usuario se as credenciais forem válidas, caso contrário, null.
     */
    Usuario verificarCredenciais(String cpf, String senhaHash);

    /**
     * Atualiza os dados de OTP (One-Time Password) para um usuário específico.
     *
     * @param idUsuario  O ID do usuário.
     * @param otp        O novo código OTP a ser salvo.
     * @param expiracao  A data e hora de expiração do novo OTP.
     */
    void atualizarOtp(int idUsuario, String otp, LocalDateTime expiracao);

    /**
     * Busca um usuário para obter seus dados de OTP.
     *
     * @param idUsuario O ID do usuário.
     * @return O objeto Usuario, que contém as informações de OTP.
     */
    Usuario buscarOtp(int idUsuario);

    /**
     * Inicia o processo de atualização de senha de um usuário.
     * A implementação deste método deve invocar a stored procedure 'atualizar_senha_usuario'.
     *
     * @param idUsuario  O ID do usuário.
     * @param novaSenha A nova senha em texto plano. A procedure cuidará da validação e do hashing.
     */
    void atualizarSenha(int idUsuario, String novaSenha);

    /**
     * Registra uma tentativa de login no sistema para fins de auditoria.
     * A implementação deste método deve invocar a stored procedure 'registrar_tentativa_login'.
     *
     * @param cpf            O CPF utilizado na tentativa de login.
     * @param sucesso        'true' se o login foi bem-sucedido, 'false' caso contrário.
     * @param infoAdicional  Qualquer informação adicional relevante (ex: endereço IP).
     */
    void registrarTentativaLogin(String cpf, boolean sucesso, String infoAdicional);
}