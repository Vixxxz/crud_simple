package dominio;

public class Logradouro extends EntidadeDominio{
    private String logradouro;
    private TipoLogradouro tpLogradouro;

    public Logradouro() {
    }

    public Logradouro(String logradouro, TipoLogradouro tpLogradouro) {
        this.logradouro = logradouro;
        this.tpLogradouro = tpLogradouro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public TipoLogradouro getTpLogradouro() {
        return tpLogradouro;
    }

    public void setTpLogradouro(TipoLogradouro tpLogradouro) {
        this.tpLogradouro = tpLogradouro;
    }

    @Override
    public String toString() {
        return "Logradouro{" +
                "logradouro='" + logradouro + '\'' +
                ", tpLogradouro=" + tpLogradouro +
                "} ";
    }
}
