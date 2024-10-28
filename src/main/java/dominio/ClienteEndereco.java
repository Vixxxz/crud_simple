package dominio;

public class ClienteEndereco extends EntidadeDominio{
    private String numero;
    private String tipoResidencia;
    private String tipoEndereco;
    private String observacoes;
    private Endereco endereco;
    private Cliente cliente;

    public ClienteEndereco() {
    }

    public ClienteEndereco(Integer id, String numero, String tipoResidencia, String tipoEndereco, String observacoes, Endereco endereco, Cliente cliente) {
        this.id = id;
        this.numero = numero;
        this.tipoResidencia = tipoResidencia;
        this.tipoEndereco = tipoEndereco;
        this.observacoes = observacoes;
        this.endereco = endereco;
        this.cliente = cliente;
    }

    public ClienteEndereco(String numero, String tipoResidencia, String tipoEndereco, String observacoes, Endereco endereco, Cliente cliente) {
        this.numero = numero;
        this.tipoResidencia = tipoResidencia;
        this.tipoEndereco = tipoEndereco;
        this.observacoes = observacoes;
        this.endereco = endereco;
        this.cliente = cliente;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipoResidencia() {
        return tipoResidencia;
    }

    public void setTipoResidencia(String tipoResidencia) {
        this.tipoResidencia = tipoResidencia;
    }

    public String getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(String tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "ClienteEndereco{" +
                "numero='" + numero + '\'' +
                ", tipoResidencia='" + tipoResidencia + '\'' +
                ", tipoEndereco='" + tipoEndereco + '\'' +
                ", observacoes='" + observacoes + '\'' +
                ", endereco=" + endereco + "} ";
    }
}
