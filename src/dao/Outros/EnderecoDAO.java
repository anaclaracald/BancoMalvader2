package dao.Outros;

import model.Endereco;

import java.util.List;

/**
 * Interface que define as operações de acesso a dados para a entidade {@link Endereco}.
 */
public interface EnderecoDAO {

    /**
     * Insere um novo endereço no banco de dados.
     * Após a inserção, o ID gerado é atribuído de volta ao objeto.
     *
     * @param endereco O objeto Endereco a ser inserido, com todos os campos preenchidos.
     */
    void inserir(Endereco endereco);

    /**
     * Busca todos os endereços associados a um ID de usuário específico.
     *
     * @param idUsuario O ID do usuário cujos endereços devem ser recuperados.
     * @return Uma lista de objetos {@link Endereco}. A lista estará vazia se nenhum endereço for encontrado.
     */
    List<Endereco> buscarPorIdUsuario(int idUsuario);

    /**
     * Atualiza os dados de um endereço existente no banco de dados.
     * A busca pelo endereço a ser atualizado é feita pelo seu ID (id_endereco).
     *
     * @param endereco O objeto Endereco com os dados atualizados.
     */
    void atualizar(Endereco endereco);

    /**
     * Exclui um endereço do banco de dados com base no seu ID.
     *
     * @param idEndereco O ID do endereço a ser excluído.
     */
    void excluir(int idEndereco);
}