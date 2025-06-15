package model.Conta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ContaCorrente extends Conta{
    private double limite;
    private LocalDate dataVencimento;
    private double taxaManutencao;

    public ContaCorrente(int idConta, String numeroConta, BigDecimal saldo, String tipoConta, LocalDateTime dataAbertura, String status, int idAgencia, int idCliente, double limite, LocalDate dataVencimento, double taxaManutencao) {
        super(idConta, numeroConta, saldo, tipoConta, dataAbertura, status, idAgencia, idCliente);
        this.limite = limite;
        this.dataVencimento = dataVencimento;
        this.taxaManutencao = taxaManutencao;
    }

    public double getLimite() {
        return limite;
    }

    public void setLimite(double limite) {
        this.limite = limite;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public double getTaxaManutencao() {
        return taxaManutencao;
    }

    public void setTaxaManutencao(double taxaManutencao) {
        this.taxaManutencao = taxaManutencao;
    }
}
