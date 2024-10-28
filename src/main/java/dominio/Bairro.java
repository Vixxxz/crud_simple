package dominio;

public class Bairro extends EntidadeDominio{
    private String bairro;
    private Cidade cidade;

    public Bairro() {
    }

    public Bairro(Integer id){
        this.id = id;
    }

    public Bairro(Integer id, String bairro, Cidade cidade) {
        super(id);
        this.bairro = bairro;
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    @Override
    public String toString() {
        return "Bairro{" +
                "bairro='" + bairro + '\'' +
                ", cidade=" + cidade +
                "} ";
    }
}
