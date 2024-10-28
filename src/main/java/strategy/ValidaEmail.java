package strategy;

import dominio.Cliente;
import dominio.EntidadeDominio;

import java.util.regex.Pattern;

public class ValidaEmail implements IStrategy{
    @Override
    public String processar(EntidadeDominio entidade, StringBuilder sb) {
        Cliente cliente = (Cliente) entidade;
        String email = cliente.getEmail();
        validaEmail(email, sb);
        return null;
    }

    private void validaEmail(String email, StringBuilder sb) {
        final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (email == null || email.isEmpty()) {
            sb.append("Email é um campo obrigatório").append("\n");
        }
        if (!Pattern.matches(emailRegex, email)){
            sb.append("Email inválido").append("\n");
        }
    }
}
