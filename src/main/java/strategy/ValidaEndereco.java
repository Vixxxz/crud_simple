package strategy;

import dominio.*;

import java.util.List;

public class ValidaEndereco implements IStrategy{
    @Override
    public String processar(EntidadeDominio entidade, StringBuilder sb) {
        ClienteEndereco enderecoRelacionado = (ClienteEndereco) entidade;

        Endereco endereco = enderecoRelacionado.getEndereco();
        Logradouro logradouro = endereco.getLogradouro();
        TipoLogradouro tipoLogradouro = logradouro.getTpLogradouro();
        Bairro bairro = endereco.getBairro();
        Cidade cidade = bairro.getCidade();
        Uf uf = cidade.getUf();
        Pais pais = uf.getPais();

        validaCampo(enderecoRelacionado.getNumero(), "O número do endereço do cliente é um campo obrigatório.", sb);
        validaCampo(enderecoRelacionado.getTipoResidencia(), "O tipo de residência do endereço do cliente é um campo obrigatório.", sb);
        validaCampo(enderecoRelacionado.getTipoEndereco(), "O tipo de endereço do cliente é um campo obrigatório.", sb);
        validaCampo(endereco.getCep(), "O CEP do endereço do cliente é um campo obrigatório.", sb);
        validaCampo(logradouro.getLogradouro(), "O logradouro do endereço do cliente é um campo obrigatório.", sb);
        validaCampo(tipoLogradouro.getTpLogradouro(), "O tipo de logradouro do endereço do cliente é um campo obrigatório.", sb);
        validaCampo(bairro.getBairro(), "O nome do bairro do endereço do cliente é um campo obrigatório.", sb);
        validaCampo(cidade.getCidade(), "O nome da cidade do endereço do cliente é um campo obrigatório.", sb);
        validaCampo(uf.getUf(), "A UF do endereço do cliente é um campo obrigatório.", sb);
        validaCampo(pais.getPais(), "O nome do país do endereço do cliente é um campo obrigatório.", sb);
        return null;
    }

    private void validaCampo(String field, String errorMessage, StringBuilder sb) {
        if (field == null || field.isEmpty()) {
            sb.append(errorMessage).append(" ");
        }
    }
}
