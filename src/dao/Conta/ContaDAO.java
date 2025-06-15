package dao.Conta;

import model.Conta.Conta;

import java.util.List;

public interface ContaDAO {
    Conta buscarPorNumero(String numeroConta);
    List<Conta> listarPorCliente(int idCliente);
    void atualizarStatus(int idConta, String novoStatus);
    void encerrarConta(int idConta, int idFuncionario, String motivo); // Chama a procedure encerrar_conta_cliente
}
