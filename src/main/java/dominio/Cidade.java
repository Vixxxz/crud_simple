package dominio;

public class Cidade extends EntidadeDominio{
    private String cidade;
    private Uf uf;

    public Cidade() {
    }

    public Cidade(String cidade, Uf uf) {
        this.cidade = cidade;
        this.uf = uf;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Uf getUf() {
        return uf;
    }

    public void setUf(Uf uf) {
        this.uf = uf;
    }

    @Override
    public String toString() {
        return "Cidade{" +
                "cidade='" + cidade + '\'' +
                ", uf=" + uf +
                "} ";
    }
}
