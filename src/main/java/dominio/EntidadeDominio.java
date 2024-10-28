package dominio;

import java.util.Date;

public class EntidadeDominio {
    protected Integer id;
    private Date dtCadastro;

    public EntidadeDominio() {
    }

    public EntidadeDominio(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(Date dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public void complementarDtCadastro(){
        dtCadastro = new Date();
    }

    @Override
    public String toString() {
        return "EntidadeDominio{" +
                "id=" + id +
                ", dtCadastro=" + dtCadastro +
                '}';
    }
}
