package fachada;

import dominio.Bandeira;
import dominio.Cliente;
import dominio.ClienteEndereco;
import dominio.EntidadeDominio;
import strategy.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Fachada implements IFachada{
    IFachada fachadaDAO = new FachadaDAO();
    public Fachada() throws SQLException, ClassNotFoundException {
    }

    @Override
    public List<EntidadeDominio> salvar(List<EntidadeDominio> entidades, StringBuilder sb) throws Exception {
        try{
            for(EntidadeDominio entidade : entidades) {
                switch (entidade) {
                    case Cliente cliente -> {
                        EncriptografaSenha criptografa = new EncriptografaSenha();
                        processarValidacoes(cliente, getValidacoes(cliente), sb);
                        cliente.setSenha(criptografa.processar(cliente, sb));
                    }
                    case ClienteEndereco clienteEndereco ->
                            processarValidacoes(clienteEndereco, getValidacoes(clienteEndereco), sb);
                    case Bandeira bandeira ->
                            processarValidacoes(bandeira, getValidacoes(bandeira), sb);
//                    case Cartao cartao -> {
//                    }
//                    case Transacao transacao -> {
//                    }
//                    case Log log -> {
//                    }
                    case null, default ->
                            throw new IllegalArgumentException("Tipo de entidade não suportado: " + entidade);
                }
            }
            if (sb.isEmpty()) {
                entidades = salvaEntidades(entidades, sb);
            } else {
                throw new Exception("Existem erros de validação: " + sb);
            }
            return entidades;
        }catch (Exception e) {
            throw new Exception(e.getMessage() + " " + entidades, e);
        }
    }

    private void processarValidacoes(EntidadeDominio entidade, List<IStrategy> estrategias, StringBuilder sb) {
        for (IStrategy strategy : estrategias) {
            strategy.processar(entidade, sb);
        }
    }

    private List<IStrategy> getValidacoes(EntidadeDominio entidade) {
        List<IStrategy> validacoes = new ArrayList<>();
        if (entidade instanceof Cliente) {
            validacoes.add(new ValidaDados());
            validacoes.add(new ValidaCpf());
            validacoes.add(new ValidaEmail());
            validacoes.add(new ValidaSenha());
            validacoes.add(new ValidaTelefone());
        } else if (entidade instanceof ClienteEndereco) {
            validacoes.add(new ValidaEndereco());
        } else if (entidade instanceof Bandeira) {
            validacoes.add(new ValidaDadosBandeira());
        }
        return validacoes;
    }

    private List<EntidadeDominio> salvaEntidades(List<EntidadeDominio> entidades, StringBuilder sb) throws Exception {
        try {
            entidades.forEach(EntidadeDominio::complementarDtCadastro);
            return fachadaDAO.salvar(entidades, sb);
        } catch (Exception e) {
            throw new Exception("Erro ao salvar: " + e.getMessage() + " " + entidades, e);
        }
    }

    @Override
    public void alterar(EntidadeDominio entidade, StringBuilder sb) {
    }

    @Override
    public void excluir(EntidadeDominio entidade, StringBuilder sb) {
    }

    @Override
    public List<EntidadeDominio> consultar(EntidadeDominio entidade, StringBuilder sb) {
        return List.of();
    }
}
