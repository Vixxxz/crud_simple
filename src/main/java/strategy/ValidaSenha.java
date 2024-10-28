package strategy;

import dominio.Cliente;
import dominio.EntidadeDominio;

public class ValidaSenha implements IStrategy{
    @Override
    public String processar(EntidadeDominio entidade, StringBuilder sb) {
        Cliente cliente = (Cliente) entidade;
        String senha = cliente.getSenha();
        validaSenhaForte(senha, sb);
        return null;
    }

    private void validaSenhaForte(String senha, StringBuilder sb) {
        final String senhaRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!senha.matches(senhaRegex) || senha.isEmpty()) {
            sb.append("Senha inválida! Deve conter no mínimo 8 caracteres, incluindo letras maiúsculas, minúsculas, números e caracteres especiais.").append("\n");
        }
    }
}
