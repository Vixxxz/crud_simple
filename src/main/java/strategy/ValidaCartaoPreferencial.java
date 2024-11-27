package strategy;

import dominio.Cartao;
import dominio.EntidadeDominio;
import fachada.FachadaDAO;

import java.util.List;

public class ValidaCartaoPreferencial implements IStrategy{
    @Override
    public String processar(EntidadeDominio entidade, StringBuilder sb) throws Exception {
        Cartao cartao = (Cartao) entidade;
        if(cartao.getPreferencial()){
            FachadaDAO fachadaDAO = new FachadaDAO();
            Cartao cartaoPreferencial = new Cartao();
            cartaoPreferencial.setPreferencial(true);
            List<EntidadeDominio> cartoes = fachadaDAO.consultar(cartaoPreferencial);
            if(!cartoes.isEmpty()){
                sb.append("Ja existe um cartao preferencial cadastrado.");
            }
        }
        return null;
    }
}
