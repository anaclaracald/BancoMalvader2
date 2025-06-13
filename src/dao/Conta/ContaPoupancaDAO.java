package dao.Usuario;

import model.Conta.ContaPoupanca;

public interface ContaPoupancaDAO {
    void inserir(ContaPoupanca conta);
    ContaPoupanca buscarPorContaId(int idConta);
    void atualizarRendimento(int idConta, double novoValor);
}
