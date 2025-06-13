package dao.Usuario;

import java.util.List;

public interface Conta {
    void inserir(Conta conta);
    Conta buscarPorId(int idConta);
    Conta buscarPorNumero(String numeroConta);
    List<Conta> listarPorCliente(int idCliente);
    void atualizarStatus(int idConta, String novoStatus);
    //VwDetalhesConta buscarDetalhes(String numeroConta); // Usa a view vw_detalhes_conta
    //List<VwResumoContas> listarResumoContas(); // Usa a view vw_resumo_contas
    void encerrarConta(int idConta, int idFuncionario, String motivo); // Chama a procedure encerrar_conta_cliente
}
