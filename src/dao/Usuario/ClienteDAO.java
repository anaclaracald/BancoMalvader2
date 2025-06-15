package dao.Usuario;

import model.Usuario.Cliente;

import java.util.List;

/**
 * Interface que define o contrato para operações de acesso a dados
 * para a entidade {@link Cliente}. Esta interface provê um conjunto
 * completo de métodos para manipulação de clientes no sistema.
 */
public interface ClienteDAO {

    /**
     * Insere um novo cliente no banco de dados.
     * A operação é transacional, registrando dados primeiro na tabela 'usuario'
     * e depois na tabela 'cliente'.
     *
     * @param cliente O objeto {@link Cliente} a ser persistido.
     */
    void inserir(Cliente cliente);

    /**
     * Busca um cliente pelo seu ID de cliente (chave primária da tabela 'cliente').
     *
     * @param idCliente O ID único do cliente.
     * @return Um objeto {@link Cliente} completamente populado se encontrado, caso contrário, retorna null.
     */
    Cliente buscarPorId(int idCliente);

    /**
     * Busca um cliente pelo ID de usuário correspondente na tabela 'usuario'.
     * Este método é fundamental para a delegação feita pelo {@link Imp.UsuarioDAOimp}.
     *
     * @param idUsuario O ID da entidade 'usuario' base.
     * @return Um objeto {@link Cliente} se encontrado, caso contrário, null.
     */
    Cliente buscarPorIdUsuario(int idUsuario);

    /**
     * Retorna uma lista com todos os clientes cadastrados no sistema.
     *
     * @return Uma {@link List} de objetos {@link Cliente}. A lista estará vazia se não houver clientes.
     */
    List<Cliente> listarTodos();

    /**
     * Invoca a Stored Procedure do banco de dados para calcular (ou recalcular)
     * o score de crédito de um cliente, com base em seu histórico de transações.
     *
     * @param idCliente O ID do cliente para o qual o score será calculado.
     */
    void calcularScoreCredito(int idCliente);
}