package dominio;

public class Cartao extends EntidadeDominio{
    private String numero;
    private String numSeguranca;
    private String nomeImpresso;
    private Boolean preferencial;
    private Bandeira bandeira;
    private Cliente cliente;

    public Cartao(Integer id, String numero, String numSeguranca, String nomeImpresso, Boolean preferencial, Bandeira bandeira, Cliente cliente) {
        super(id);
        this.numero = numero;
        this.numSeguranca = numSeguranca;
        this.nomeImpresso = nomeImpresso;
        this.preferencial = preferencial;
        this.bandeira = bandeira;
        this.cliente = cliente;
    }

    public Cartao (){}

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNumSeguranca() {
        return numSeguranca;
    }

    public void setNumSeguranca(String numSeguranca) {
        this.numSeguranca = numSeguranca;
    }

    public String getNomeImpresso() {
        return nomeImpresso;
    }

    public void setNomeImpresso(String nomeImpresso) {
        this.nomeImpresso = nomeImpresso;
    }

    public Boolean getPreferencial() {
        return preferencial;
    }

    public void setPreferencial(Boolean preferencial) {
        this.preferencial = preferencial;
    }

    public Bandeira getBandeira() {
        return bandeira;
    }

    public void setBandeira(Bandeira bandeira) {
        this.bandeira = bandeira;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
