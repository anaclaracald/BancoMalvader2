package dao.Usuario;

public interface ContaCorrenteDAO {
    void inserir(ContaCorrenteDAO conta);
    ContaCorrenteDAO buscarPorContaId(int idConta);
    void atualizarLimite(int idConta, double novoLimite);
}
