package strategy;

import dominio.Cartao;
import dominio.EntidadeDominio;
import fachada.FachadaDAO;

import java.util.List;

public class ValidaCartaoPreferencial implements IStrategy{
    @Override
    public String processar(EntidadeDominio entidade, StringBuilder sb) throws Exception {
        Cartao cartao = new Cartao();
        cartao.setPreferencial(true);
        FachadaDAO fachadaDAO = new FachadaDAO();
        List<EntidadeDominio> cartoes = fachadaDAO.consultar(cartao);
        if(!cartoes.isEmpty()){
            sb.append("Ja existe um cartao preferencial cadastrado.");
        }
        return null;
    }
}
