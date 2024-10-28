package dominio;

public class Uf extends EntidadeDominio{
    private String uf;
    private Pais pais;

    public Uf() {
    }

    public Uf(Pais pais, String uf) {
        this.pais = pais;
        this.uf = uf;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    @Override
    public String toString() {
        return "Uf{" +
                "uf='" + uf + '\'' +
                ", pais=" + pais +
                "} ";
    }
}
