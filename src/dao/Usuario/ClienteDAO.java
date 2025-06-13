package dao.Conta.Usuario;

import model.Usuario.Cliente;

public interface ClienteDAO {
    void inserir(Cliente cliente);
    Cliente buscarPorId(int id);
    Cliente buscarPorUsuarioId(int idUsuario);
    void atualizarScore(int idCliente, double novoScore);
}
