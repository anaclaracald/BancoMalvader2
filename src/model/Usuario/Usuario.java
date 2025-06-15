package model.Usuario;

import java.time.LocalDateTime;

public abstract class Usuario {
    protected int idUsuario;
    protected String nome;
    protected String cpf;
    protected String dataNascimento; // ou LocalDate
    protected String telefone;
    protected String tipoUsuario; // CLIENTE ou FUNCIONARIO
    protected String senhaHash;
    protected String otpAtivo;
    protected LocalDateTime otpExpiracao;

    public Usuario(int idUsuario, String nome, String cpf, String dataNascimento, String telefone, String tipoUsuario, String senhaHash, String otpAtivo, LocalDateTime otpExpiracao) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.tipoUsuario = tipoUsuario;
        this.senhaHash = senhaHash;
        this.otpAtivo = otpAtivo;
        this.otpExpiracao = otpExpiracao;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getOtpAtivo() {
        return otpAtivo;
    }

    public void setOtpAtivo(String otpAtivo) {
        this.otpAtivo = otpAtivo;
    }

    public LocalDateTime getOtpExpiracao() {
        return otpExpiracao;
    }

    public void setOtpExpiracao(LocalDateTime otpExpiracao) {
        this.otpExpiracao = otpExpiracao;
    }
}
