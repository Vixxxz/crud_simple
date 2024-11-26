package strategy;

import dominio.Cartao;
import dominio.EntidadeDominio;

public class ValidaDadosCartao implements IStrategy {
    @Override
    public String processar(EntidadeDominio entidade, StringBuilder sb) {
        Cartao cartao = (Cartao) entidade;

        if (isStringValida(cartao.getNumero())) {
            sb.append("O número do cartão é obrigatório.\n");
        } else if (!cartao.getNumero().matches("\\d{16}")) {
            sb.append("O número do cartão deve conter exatamente 16 dígitos.\n");
        }

        if (isStringValida(cartao.getNumSeguranca())) {
            sb.append("O número de segurança é obrigatório.\n");
        } else if (!cartao.getNumSeguranca().matches("\\d{3}")) {
            sb.append("O número de segurança deve conter exatamente 3 dígitos.\n");
        }

        if (isStringValida(cartao.getNomeImpresso())) {
            sb.append("O nome impresso no cartão é obrigatório.\n");
        }

        if (cartao.getBandeira() == null) {
            sb.append("A bandeira do cartão é obrigatória.\n");
        }

        if (cartao.getCliente() == null) {
            sb.append("O cliente associado ao cartão é obrigatório.\n");
        }

        return null;
    }

    private boolean isStringValida(String value) {
        return value != null && !value.isBlank();
    }
}
