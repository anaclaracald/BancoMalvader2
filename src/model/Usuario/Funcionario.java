package model.Usuario;

import java.time.LocalDateTime;

public class Funcionario extends Usuario{
    private int idFuncionario;
    private String codigoFuncionario;
    private Cargo cargo;
    private Integer idSupervisor;
    private Integer idAgencia;

    public Funcionario(int idUsuario, String nome, String cpf, String dataNascimento, String telefone, String tipoUsuario, String senhaHash, String otpAtivo, LocalDateTime otpExpiracao, int idFuncionario, String codigoFuncionario, Cargo cargo, Integer idSupervisor, Integer idAgencia) {
        super(idUsuario, nome, cpf, dataNascimento, telefone, tipoUsuario, senhaHash, otpAtivo, otpExpiracao);
        this.idFuncionario = idFuncionario;
        this.codigoFuncionario = codigoFuncionario;
        this.cargo = cargo;
        this.idSupervisor = idSupervisor;
        this.idAgencia = idAgencia;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public String getCodigoFuncionario() {
        return codigoFuncionario;
    }

    public void setCodigoFuncionario(String codigoFuncionario) {
        this.codigoFuncionario = codigoFuncionario;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Integer getIdSupervisor() {
        return idSupervisor;
    }

    public void setIdSupervisor(Integer idSupervisor) {
        this.idSupervisor = idSupervisor;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public Integer getIdAgencia() {
        return idAgencia;
    }

    public void setIdAgencia(Integer idAgencia) {
        this.idAgencia = idAgencia;
    }
}
