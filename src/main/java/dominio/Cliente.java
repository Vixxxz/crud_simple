package dominio;

import java.util.Date;
import java.util.List;

public class Cliente extends EntidadeDominio{
    private String ranking = "1";
    private String nome;
    private String genero;
    private String cpf;
    private String tipoTelefone;
    private String telefone;
    private String email;
    private String senha;
    private Date dataNascimento;


    public Cliente() {
    }

    public Cliente(Integer id){
        this.id = id;
    }

    public Cliente(String ranking, String nome, String genero, String cpf, String tipoTelefone, String telefone, String email, String senha, Date dataNascimento, List<ClienteEndereco> enderecosRelacionados) {
        this.ranking = ranking;
        this.nome = nome;
        this.genero = genero;
        this.cpf = cpf;
        this.tipoTelefone = tipoTelefone;
        this.telefone = telefone;
        this.email = email;
        this.senha = senha;
        this.dataNascimento = dataNascimento;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTipoTelefone() {
        return tipoTelefone;
    }

    public void setTipoTelefone(String tipoTelefone) {
        this.tipoTelefone = tipoTelefone;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "ranking='" + ranking + '\'' +
                ", nome='" + nome + '\'' +
                ", genero='" + genero + '\'' +
                ", cpf='" + cpf + '\'' +
                ", tipoTelefone='" + tipoTelefone + '\'' +
                ", telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", dataNascimento=" + dataNascimento + "} ";
    }
}
