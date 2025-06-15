package dao.Outros;

import model.Transacao;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface que define as operações de acesso a dados para a entidade {@link Transacao}.
 */
public interface TransacaoDAO {

    /**
     * Insere uma nova transação financeira no banco de dados.
     * A atualização dos saldos das contas envolvidas é feita automaticamente
     * por um gatilho (trigger) no banco de dados.
     *
     * @param transacao O objeto Transacao a ser registrado.
     */
    void inserir(Transacao transacao);

    /**
     * Lista as últimas transações de uma conta específica, limitado pela quantidade fornecida.
     * Útil para exibir um mini-extrato.
     *
     * @param idConta O ID da conta para a qual o extrato será gerado.
     * @param limite O número máximo de transações a serem retornadas.
     * @return Uma lista de objetos {@link Transacao}.
     */
    List<Transacao> listarPorConta(int idConta, int limite);

    /**
     * Lista todas as transações de uma conta dentro de um período de tempo específico.
     * Útil para gerar extratos mensais ou personalizados.
     *
     * @param idConta O ID da conta.
     * @param inicio  A data e hora de início do período.
     * @param fim     A data e hora de fim do período.
     * @return Uma lista de objetos {@link Transacao}.
     */
    List<Transacao> listarPorPeriodo(int idConta, LocalDateTime inicio, LocalDateTime fim);
}