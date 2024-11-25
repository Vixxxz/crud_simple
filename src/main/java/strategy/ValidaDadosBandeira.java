package strategy;

import dominio.Bandeira;
import dominio.EntidadeDominio;

public class ValidaDadosBandeira implements IStrategy{
    @Override
    public String processar(EntidadeDominio entidade, StringBuilder sb) {
        Bandeira bandeira = (Bandeira) entidade;

        if (bandeira.getNomeBandeira() == null || bandeira.getNomeBandeira().isBlank()) {
            sb.append("Nome da bandeira é obrigatório.");
            return sb.toString();
        }
        return null;
    }
}
