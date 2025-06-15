package model.Conta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ContaPoupanca extends Conta{
    private double taxaRendimento;
    private LocalDateTime ultimoRendimento;

    public ContaPoupanca(int idConta, String numeroConta, BigDecimal saldo, String tipoConta, LocalDateTime dataAbertura, String status, int idAgencia, int idCliente, double taxaRendimento, LocalDateTime ultimoRendimento) {
        super(idConta, numeroConta, saldo, tipoConta, dataAbertura, status, idAgencia, idCliente);
        this.taxaRendimento = taxaRendimento;
        this.ultimoRendimento = ultimoRendimento;
    }

    public double getTaxaRendimento() {
        return taxaRendimento;
    }

    public void setTaxaRendimento(double taxaRendimento) {
        this.taxaRendimento = taxaRendimento;
    }

    public LocalDateTime getUltimoRendimento() {
        return ultimoRendimento;
    }

    public void setUltimoRendimento(LocalDateTime ultimoRendimento) {
        this.ultimoRendimento = ultimoRendimento;
    }
}
