package dominio;

public class Pais extends EntidadeDominio{
    private String pais;

    public Pais(String pais) {
        this.pais = pais;
    }

    public Pais() {
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    @Override
    public String toString() {
        return "Pais{" +
                "pais='" + pais + '\'' +
                "} ";
    }
}
