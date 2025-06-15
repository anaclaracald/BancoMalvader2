package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transacao {
    private int idTransacao;
    private Integer idContaOrigem; // Usar Integer para permitir valor nulo
    private Integer idContaDestino; // Usar Integer para permitir valor nulo
    private TipoTransacao tipoTransacao;
    private BigDecimal valor;
    private LocalDateTime dataHora;
    private String descricao;

    public Transacao(int idTransacao, Integer idContaOrigem, Integer idContaDestino, TipoTransacao tipoTransacao, BigDecimal valor, LocalDateTime dataHora, String descricao) {
        this.idTransacao = idTransacao;
        this.idContaOrigem = idContaOrigem;
        this.idContaDestino = idContaDestino;
        this.tipoTransacao = tipoTransacao;
        this.valor = valor;
        this.dataHora = dataHora;
        this.descricao = descricao;
    }

    public int getIdTransacao() {
        return idTransacao;
    }

    public void setIdTransacao(int idTransacao) {
        this.idTransacao = idTransacao;
    }

    public Integer getIdContaOrigem() {
        return idContaOrigem;
    }

    public void setIdContaOrigem(Integer idContaOrigem) {
        this.idContaOrigem = idContaOrigem;
    }

    public Integer getIdContaDestino() {
        return idContaDestino;
    }

    public void setIdContaDestino(Integer idContaDestino) {
        this.idContaDestino = idContaDestino;
    }

    public TipoTransacao getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(TipoTransacao tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
