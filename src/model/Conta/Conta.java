package model.Conta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class Conta {
    protected int idConta;
    protected String numeroConta;
    protected BigDecimal saldo;
    protected String tipoConta;
    protected LocalDateTime dataAbertura;
    protected String status;
    protected int idAgencia;
    protected int idCliente;
    protected Integer idFuncionarioAbertura;

    public Conta(int idConta, String numeroConta, BigDecimal saldo, String tipoConta, LocalDateTime dataAbertura, String status, int idAgencia, int idCliente) {
        this.idConta = idConta;
        this.numeroConta = numeroConta;
        this.saldo = saldo;
        this.tipoConta = tipoConta;
        this.dataAbertura = dataAbertura;
        this.status = status;
        this.idAgencia = idAgencia;
        this.idCliente = idCliente;
        this.idFuncionarioAbertura = idFuncionarioAbertura;
    }

    public Integer getIdFuncionarioAbertura() {
        return idFuncionarioAbertura;
    }

    public void setIdFuncionarioAbertura(Integer idFuncionarioAbertura) {
        this.idFuncionarioAbertura = idFuncionarioAbertura;
    }

    public int getIdConta() {
        return idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(String tipoConta) {
        this.tipoConta = tipoConta;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIdAgencia() {
        return idAgencia;
    }

    public void setIdAgencia(int idAgencia) {
        this.idAgencia = idAgencia;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
}
