package dao.Usuario;

import model.Endereco;

import java.util.List;

public interface EnderecoDAO {
    void inserir(Endereco endereco);
    List<Endereco> buscarPorIdUsuario(int idUsuario);
    void atualizar(Endereco endereco);
    void excluir(int idEndereco);
}
