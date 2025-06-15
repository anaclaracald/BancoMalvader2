package dao.Outros;

import model.Auditoria;

/**
 * Interface que define o contrato para operações de acesso a dados
 * para a entidade {@link Auditoria}. O propósito principal é registrar
 * eventos para fins de segurança e rastreabilidade.
 */
public interface AuditoriaDAO {

    /**
     * Insere um novo registro de auditoria no banco de dados.
     * Esta é uma operação de "apenas escrita" (append-only).
     *
     * @param auditoria O objeto {@link Auditoria} contendo os detalhes do evento a ser registrado.
     */
    void inserir(Auditoria auditoria);
}