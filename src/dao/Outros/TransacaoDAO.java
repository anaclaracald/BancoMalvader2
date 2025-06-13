package dao.Usuario;

import model.Transacao;

import java.time.LocalDateTime;
import java.util.List;

public interface TransacaoDAO {
    void inserir(Transacao transacao);
    List<Transacao> listarPorConta(int idConta, int limite);
    List<Transacao> listarPorPeriodo(int idConta, LocalDateTime inicio, LocalDateTime fim);
}
