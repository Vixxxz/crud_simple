package strategy;

import dao.BandeiraDAO;
import dominio.Bandeira;
import dominio.Cartao;
import dominio.EntidadeDominio;
import fachada.FachadaDAO;

import java.util.List;

public class ValidaBandeiraExistente implements IStrategy{
    @Override
    public String processar(EntidadeDominio entidade, StringBuilder sb) throws Exception {
        Cartao cartao = (Cartao) entidade;
        Bandeira bandeira = cartao.getBandeira();
        FachadaDAO fachadaDAO = new FachadaDAO();
        List<EntidadeDominio> cartoes = fachadaDAO.consultar(bandeira, sb);
        if(cartoes.isEmpty()){
            sb.append("A bandeira deve estar cadastrada no sitema.");
        }
        return null;
    }
}
