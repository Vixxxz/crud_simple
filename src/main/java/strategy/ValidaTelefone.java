package strategy;

import dominio.Cliente;
import dominio.EntidadeDominio;

import java.util.regex.Pattern;

public class ValidaTelefone implements IStrategy{
    @Override
    public String processar(EntidadeDominio entidade, StringBuilder sb) {
        Cliente cliente = (Cliente) entidade;
        String telefone = cliente.getTelefone();
        validaTelefone(telefone, sb);
        return null;
    }

    private void validaTelefone(String telefone, StringBuilder sb) {
        final String TELEFONE_REGEX = "^(\\(\\d{2}\\)\\s)?(9\\d{4}-?\\d{4}|\\d{4}-?\\d{4}|\\d{11})$";
        if (telefone == null || telefone.isEmpty()) {
            sb.append("Telefone é um campo obrigatório").append("\n"); // Retorna falso se o telefone for nulo ou vazio
        }
        if(!Pattern.matches(TELEFONE_REGEX, telefone)){
            sb.append("Telefone inválido").append("\n");
        }
    }
}
