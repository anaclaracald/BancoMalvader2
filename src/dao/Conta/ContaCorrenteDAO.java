package dao.Conta;

import model.Conta.ContaCorrente;

public interface ContaCorrenteDAO {
    /**
     * Insere uma nova conta corrente no banco de dados.
     * @param conta O objeto ContaCorrente a ser inserido.
     */
    void inserir(ContaCorrente conta);

    /**
     * Busca uma conta corrente pelo ID da conta.
     * @param idConta O ID da conta.
     * @return O objeto ContaCorrente populado com os dados.
     */
    ContaCorrente buscarPorContaId(int idConta);

    /**
     * Atualiza o valor do limite da conta corrente.
     * @param idConta O ID da conta.
     * @param novoLimite O novo valor para o limite.
     */
    void atualizarLimite(int idConta, double novoLimite);
}