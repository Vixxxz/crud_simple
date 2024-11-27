package fachada;

import dominio.*;
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
                    case Cartao cartao ->
                            processarValidacoes(cartao, getValidacoes(cartao), sb);
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

    private void processarValidacoes(EntidadeDominio entidade, List<IStrategy> estrategias, StringBuilder sb) throws Exception {
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
        } else if (entidade instanceof Cartao) {
            validacoes.add(new ValidaDadosCartao());
            validacoes.add(new ValidaBandeiraExistente());
            validacoes.add(new ValidaCartaoPreferencial());
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
        try{
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
                case Cartao cartao ->
                        processarValidacoes(cartao, getValidacoes(cartao), sb);
    //                    }
    //                    case Transacao transacao -> {
    //                    }
    //                    case Log log -> {
    //                    }
                case null, default ->
                        throw new IllegalArgumentException("Tipo de entidade não suportado: " + entidade);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(EntidadeDominio entidade, StringBuilder sb) {
    }

    @Override
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) throws Exception {
        try {
            List<EntidadeDominio> entidades = new ArrayList<>();
            switch (entidade) {
                case Cliente cliente -> entidades = consultaCliente(cliente);
                case ClienteEndereco clienteEndereco -> entidades = consultaClienteEndereco(clienteEndereco);
                case Bandeira bandeira -> entidades = consultaBandeira(bandeira);
                case Cartao cartao -> entidades = consultaCartao(cartao);
//                    }
//                    case Transacao transacao -> {
//                    }
//                    case Log log -> {
//                    }
                case null, default ->
                        throw new IllegalArgumentException("Tipo de entidade não suportado: " + entidade);
            }
            return entidades;
        }catch (Exception e) {
            throw new Exception(e.getMessage() + " " + entidade, e);
        }
    }

    private List<EntidadeDominio> consultaCartao(Cartao cartao) throws Exception {
        try {
            return fachadaDAO.consultar(cartao);
        } catch (Exception e) {
            throw new Exception("Erro ao consultar o cartão: " + e.getMessage(), e);
        }
    }

    private List<EntidadeDominio> consultaBandeira(Bandeira bandeira) throws Exception {
        try {
            return fachadaDAO.consultar(bandeira);
        } catch (Exception e) {
            throw new Exception("Erro ao consultar o cartão: " + e.getMessage(), e);
        }
    }

    private List<EntidadeDominio> consultaClienteEndereco(ClienteEndereco clienteEndereco) throws Exception {
        try {
            return fachadaDAO.consultar(clienteEndereco);
        } catch (Exception e) {
            throw new Exception("Erro ao consultar o cartão: " + e.getMessage(), e);
        }
    }

    private List<EntidadeDominio> consultaCliente(Cliente cliente) throws Exception {
        try {
            return fachadaDAO.consultar(cliente);
        } catch (Exception e) {
            throw new Exception("Erro ao consultar o cartão: " + e.getMessage(), e);
        }
    }
}
