package dao.Outros;

import model.Relatorio;

/**
 * Interface que define o contrato para operações de acesso a dados
 * para a entidade {@link Relatorio}.
 */
public interface RelatorioDAO {

    /**
     * Salva um novo relatório gerado no banco de dados.
     *
     * @param relatorio O objeto {@link Relatorio} contendo o conteúdo e metadados do relatório.
     */
    void inserir(Relatorio relatorio);
}