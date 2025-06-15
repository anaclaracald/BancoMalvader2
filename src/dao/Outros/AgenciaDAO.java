package dao.Outros;

import model.Agencia;

import java.util.List;

public interface AgenciaDAO {
    void inserir(Agencia agencia);
    Agencia buscarPorId(int idAgencia);
    Agencia buscarPorCodigo(String codigoAgencia);
    List<Agencia> listarTodas();
}
