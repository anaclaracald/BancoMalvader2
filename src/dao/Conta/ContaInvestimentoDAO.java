package dao.Conta;

import model.Conta.ContaInvestimento;

public interface ContaInvestimentoDAO {
    /**
     * Insere uma nova conta de investimento no banco de dados.
     * A operação é transacional, inserindo dados nas tabelas 'conta' e 'conta_investimento'.
     * @param conta O objeto ContaInvestimento a ser inserido.
     */
    void inserir(ContaInvestimento conta);

    /**
     * Busca uma conta de investimento pelo seu ID.
     * @param idConta O ID da conta a ser buscada.
     * @return O objeto ContaInvestimento totalmente populado, ou null se não for encontrado.
     */
    ContaInvestimento buscarPorContaId(int idConta);
}