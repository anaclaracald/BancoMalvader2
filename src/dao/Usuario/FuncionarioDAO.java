package dao.Usuario;

import model.Usuario.Funcionario;

import java.util.List;

/**
 * Interface que define o contrato para operações de acesso a dados
 * para a entidade {@link Funcionario}. Esta interface provê um conjunto
 * completo de métodos para manipulação de funcionários no sistema.
 */
public interface FuncionarioDAO {

    /**
     * Insere um novo funcionário no banco de dados.
     * Esta operação é transacional, envolvendo inserções nas tabelas 'usuario' e 'funcionario'.
     *
     * @param funcionario O objeto {@link Funcionario} a ser persistido. Deve conter todos os dados necessários.
     */
    void inserir(Funcionario funcionario);

    /**
     * Busca um funcionário pelo seu ID de funcionário (chave primária da tabela 'funcionario').
     *
     * @param idFuncionario O ID único do funcionário.
     * @return Um objeto {@link Funcionario} completamente populado se encontrado, caso contrário, retorna null.
     */
    Funcionario buscarPorId(int idFuncionario);

    /**
     * Busca um funcionário pelo seu código de identificação único.
     *
     * @param codigoFuncionario O código alfanumérico único do funcionário.
     * @return Um objeto {@link Funcionario} se encontrado, caso contrário, null.
     */
    Funcionario buscarPorCodigo(String codigoFuncionario);

    /**
     * Busca um funcionário pelo ID de usuário associado.
     * Essencial para a delegação feita pela {@link Imp.UsuarioDAOimp}.
     *
     * @param idUsuario O ID da entidade 'usuario' base.
     * @return Um objeto {@link Funcionario} se encontrado, caso contrário, null.
     */
    Funcionario buscarPorIdUsuario(int idUsuario);

    /**
     * Retorna uma lista com todos os funcionários cadastrados no sistema.
     *
     * @return Uma {@link List} de objetos {@link Funcionario}. A lista estará vazia se não houver funcionários.
     */
    List<Funcionario> listarTodos();

    /**
     * Atualiza os dados de um funcionário existente.
     * Esta operação é transacional, podendo afetar as tabelas 'usuario' e 'funcionario'.
     *
     * @param funcionario O objeto {@link Funcionario} com os dados atualizados. O ID do funcionário e do usuário são usados para localizar o registro.
     */
    void atualizar(Funcionario funcionario);
}