package dominio;

public class TipoLogradouro extends EntidadeDominio{
    private String tpLogradouro;

    public TipoLogradouro() {
    }

    public TipoLogradouro(String tpLogradouro) {
        this.tpLogradouro = tpLogradouro;
    }

    public String getTpLogradouro() {
        return tpLogradouro;
    }

    public void setTpLogradouro(String tpLogradouro) {
        this.tpLogradouro = tpLogradouro;
    }

    @Override
    public String toString() {
        return "TipoLogradouro{" +
                "tpLogradouro='" + tpLogradouro + '\'' +
                "} ";
    }
}
