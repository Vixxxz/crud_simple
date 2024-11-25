package dominio;

public class Bandeira extends EntidadeDominio{
    private String nomeBandeira;

    public Bandeira() {
    }

    public Bandeira(Integer id, String nomeBandeira) {
        super(id);
        this.nomeBandeira = nomeBandeira;
    }

    public Bandeira(String nomeBandeira){
        this.nomeBandeira = nomeBandeira;
    }

    public String getNomeBandeira() {
        return nomeBandeira;
    }

    public void setNomeBandeira(String nomeBandeira) {
        this.nomeBandeira = nomeBandeira;
    }
}
