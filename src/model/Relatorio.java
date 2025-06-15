package model;

import java.time.LocalDateTime;

public class Relatorio {
    private int idRelatorio;
    private LocalDateTime dataGeracao;
    private String tipoRelatorio;
    private String conteudo;
    private int idFuncionario;

    public Relatorio(int idRelatorio, LocalDateTime dataGeracao, String tipoRelatorio, String conteudo, int idFuncionario) {
        this.idRelatorio = idRelatorio;
        this.dataGeracao = dataGeracao;
        this.tipoRelatorio = tipoRelatorio;
        this.conteudo = conteudo;
        this.idFuncionario = idFuncionario;
    }

    public int getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(int idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public String getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(String tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.idFuncionario = idFuncionario;
    }
}
