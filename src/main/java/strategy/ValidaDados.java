package strategy;

import dominio.Cliente;
import dominio.EntidadeDominio;

import java.util.Date;

public class ValidaDados implements IStrategy{
    @Override
    public String processar(EntidadeDominio entidade, StringBuilder sb) {
        Cliente cliente = (Cliente) entidade;
        validaCampo(cliente.getNome(), "Nome é um campo obrigatório", sb);
        validaCampo(cliente.getGenero(), "Genero é um campo obrigatório", sb);
        validaCampo(cliente.getTipoTelefone(), "Tipo Telefone é um campo obrigatório", sb);
        validaData(cliente.getDataNascimento(), "Nascimento é um campo obrigatório", sb);

        if(!sb.isEmpty()){
            return sb.toString();
        }else{
            return null;
        }
    }

    private void validaData(Date data, String mensagemErro, StringBuilder sb) {
        if(data == null) {
            sb.append(mensagemErro).append(" ");
            return;
        }
        if(data.after(new Date())){
            sb.append("Data de Nascimento não pode ser futura").append(" ");
        }
    }

    private void validaCampo(String field, String mensagemErro, StringBuilder sb) {
        if (field == null || field.isEmpty()) {
            sb.append(mensagemErro).append(" ");
        }
    }
}
