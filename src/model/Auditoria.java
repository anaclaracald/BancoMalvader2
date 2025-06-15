package model;

import java.time.LocalDateTime;

public class Auditoria {
    private int idAuditoria;
    private String acao;
    private LocalDateTime dataHora;
    private String detalhes;
    private int idUsuario;

    public Auditoria(int idAuditoria, String acao, LocalDateTime dataHora, String detalhes, int idUsuario) {
        this.idAuditoria = idAuditoria;
        this.acao = acao;
        this.dataHora = dataHora;
        this.detalhes = detalhes;
        this.idUsuario = idUsuario;
    }

    public int getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(int idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
