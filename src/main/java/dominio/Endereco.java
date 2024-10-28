package dominio;

public class Endereco extends EntidadeDominio{
    private String cep;
    private Bairro bairro;
    private Logradouro logradouro;

    public Endereco() {
    }

    public Endereco(Integer id, String cep, Bairro bairro, Logradouro logradouro) {
        this.id = id;
        this.cep = cep;
        this.bairro = bairro;
        this.logradouro = logradouro;
    }

    public Endereco(String cep, Bairro bairro, Logradouro logradouro) {
        this.cep = cep;
        this.bairro = bairro;
        this.logradouro = logradouro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    @Override
    public String toString() {
        return "Endereco{" +
                "cep='" + cep + '\'' +
                ", bairro=" + bairro +
                ", logradouro=" + logradouro +
                "} ";
    }
}
