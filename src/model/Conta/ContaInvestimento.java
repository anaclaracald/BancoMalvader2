package model.Conta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ContaInvestimento extends Conta{
    private String perfilRisco;
    private BigDecimal valorMinimo;
    private double taxaRendimentoBase;

    public ContaInvestimento(int idConta, String numeroConta, BigDecimal saldo, String tipoConta, LocalDateTime dataAbertura, String status, int idAgencia, int idCliente, String perfilRisco, BigDecimal valorMinimo, double taxaRendimentoBase) {
        super(idConta, numeroConta, saldo, tipoConta, dataAbertura, status, idAgencia, idCliente);
        this.perfilRisco = perfilRisco;
        this.valorMinimo = valorMinimo;
        this.taxaRendimentoBase = taxaRendimentoBase;
    }

    public String getPerfilRisco() {
        return perfilRisco;
    }

    public void setPerfilRisco(String perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

    public BigDecimal getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(BigDecimal valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public double getTaxaRendimentoBase() {
        return taxaRendimentoBase;
    }

    public void setTaxaRendimentoBase(double taxaRendimentoBase) {
        this.taxaRendimentoBase = taxaRendimentoBase;
    }
}
