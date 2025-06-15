package model.Usuario;

import java.time.LocalDateTime;

public class Cliente extends Usuario {
    private int idCliente;
    private double scoreCredito;

    public Cliente(int idUsuario, String nome, String cpf, String dataNascimento, String telefone, String tipoUsuario, String senhaHash, String otpAtivo, LocalDateTime otpExpiracao, int idCliente, double scoreCredito) {
        super(idUsuario, nome, cpf, dataNascimento, telefone, tipoUsuario, senhaHash, otpAtivo, otpExpiracao);
        this.idCliente = idCliente;
        this.scoreCredito = scoreCredito;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public double getScoreCredito() {
        return scoreCredito;
    }

    public void setScoreCredito(double scoreCredito) {
        this.scoreCredito = scoreCredito;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
}
