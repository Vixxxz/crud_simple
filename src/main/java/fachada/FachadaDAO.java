package fachada;

import dao.*;
import dominio.*;
import strategy.EncriptografaSenha;
import util.Conexao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FachadaDAO implements IFachada {
    private Connection connection;
    private static final Logger logger = Logger.getLogger(FachadaDAO.class.getName());

    public FachadaDAO() throws SQLException, ClassNotFoundException {
        this.connection = Conexao.getConnectionMySQL();
    }

    public List<EntidadeDominio> salvar(List<EntidadeDominio> entidades, StringBuilder sb) throws Exception {
        try{
            if(connection == null || connection.isClosed()){
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);
            List<EntidadeDominio> entidadesSalvas = new ArrayList<>();
            for(EntidadeDominio entidade : entidades){
                switch (entidade) {
                    case Cliente cliente -> salvaCliente(cliente, entidadesSalvas, entidades);
                    case ClienteEndereco clienteEndereco -> salvaClienteEndereco(clienteEndereco, entidadesSalvas);
                    case Bandeira bandeira -> salvaBandeira(bandeira, entidadesSalvas);
                    case Cartao cartao -> salvaCartao(cartao, entidadesSalvas);
//                    }
//                    case Transacao transacao -> {
//                    }
//                    case Log log -> {
//                    }
                    case null, default ->
                            throw new IllegalArgumentException("Tipo de entidade não suportado: " + entidade);
                }
            }
            connection.commit();
            return entidadesSalvas;
        }catch (Exception e){
            try {
                connection.rollback();
                logger.log(Level.INFO, "Rollback realizado");
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Erro ao tentar realizar o rollback " + rollbackEx.getMessage(), rollbackEx);
            }
            logger.log(Level.SEVERE, e.getMessage() + " " + entidades, e);
            throw new Exception(e.getMessage() + " " + entidades, e);
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqlEx) {
                    logger.log(Level.SEVERE, "Erro ao tentar fechar a conexão: " + sqlEx.getMessage(), sqlEx);
                }
            }
        }
    }

    private void salvaCartao(Cartao cartao, List<EntidadeDominio> entidadesSalvas) throws Exception {
        IDAO cartaoDAO = new CartaoDAO(connection);
        List<EntidadeDominio> cartoesSalvos = cartaoDAO.consultar(cartao);

        if (cartoesSalvos.isEmpty()) {
            Cartao cartaoSalvo = (Cartao) cartaoDAO.salvar(cartao);
            logger.log(Level.INFO, "Cartão salvo: " + cartaoSalvo.getNumero());
            entidadesSalvas.add(cartaoSalvo);
        } else {
            throw new Exception("Cartão já existente: " + cartao.getNumero());
        }
    }

    private void salvaBandeira(Bandeira bandeira, List<EntidadeDominio> entidadesSalvas) throws Exception {
        IDAO bandeiraDAO = new BandeiraDAO(connection);
        List<EntidadeDominio> bandeirasSalvas = bandeiraDAO.consultar(bandeira);

        if (bandeirasSalvas.isEmpty()) {
            bandeira = (Bandeira) bandeiraDAO.salvar(bandeira);
            logger.log(Level.INFO, "Bandeira salva: " + bandeira.getNomeBandeira());
            entidadesSalvas.add(bandeira);
        } else {
            throw new Exception("Bandeira já existente: " + bandeira.getNomeBandeira());
        }
    }

    private void salvaCliente(Cliente cliente, List<EntidadeDominio> entidadesSalvas, List<EntidadeDominio> entidades) throws Exception {
        IDAO clienteDAO = new ClienteDAO(connection);
        List<EntidadeDominio> clientesSalvos = clienteDAO.consultar(cliente);

        if (clientesSalvos.isEmpty()) {
            Cliente clienteSalvo = (Cliente) clienteDAO.salvar(cliente);
            logger.log(Level.INFO, "Cliente salvo: " + clienteSalvo.getCpf());

            entidades.stream()
                    .filter(e -> e instanceof ClienteEndereco)
                    .forEach(e -> ((ClienteEndereco) e).setCliente(clienteSalvo));

            entidadesSalvas.add(clienteSalvo);
        } else {
            throw new Exception("Cliente já existente: CPF " + cliente.getCpf());
        }
    }

    private void salvaClienteEndereco(ClienteEndereco clienteEndereco, List<EntidadeDominio> entidadesSalvas) throws Exception {
        IDAO clienteEnderecoDAO = new ClienteEnderecoDAO(connection);
        List<EntidadeDominio> cliEndSalvos = clienteEnderecoDAO.consultar(clienteEndereco);

        if (cliEndSalvos.isEmpty()) {
            clienteEndereco = (ClienteEndereco) clienteEnderecoDAO.salvar(clienteEndereco);
            logger.log(Level.INFO, "Cliente endereço salvo: Número " + clienteEndereco.getNumero());
            entidadesSalvas.add(clienteEndereco);
        } else {
            throw new Exception("Cliente Endereço já existente ");
        }
    }

    public void alterar(EntidadeDominio entidade, StringBuilder sb) throws Exception {
        try{
            switch (entidade) {
                case Cliente cliente -> alteraCliente(cliente);
                case ClienteEndereco clienteEndereco -> alteraClienteEndereco(clienteEndereco);
                case Bandeira bandeira -> alteraBandeira(bandeira);
                case Cartao cartao -> alteraCartao(cartao);
//                    }
//                    case Transacao transacao -> {
//                    }
//                    case Log log -> {
//                    }
                case null, default ->
                        throw new IllegalArgumentException("Tipo de entidade não suportado: " + entidade);
            }
        } catch (Exception e) {
            throw new Exception("Erro ao alterar: " + e.getMessage(), e);
        }
    }

    private void alteraCartao(Cartao cartao) throws Exception {
        try{
            CartaoDAO cartaoDAO = new CartaoDAO(connection);
            cartaoDAO.alterar(cartao);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    private void alteraBandeira(Bandeira bandeira) throws Exception {
        try{
            BandeiraDAO bandeiraDAO = new BandeiraDAO(connection);
            bandeiraDAO.alterar(bandeira);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    private void alteraClienteEndereco(ClienteEndereco clienteEndereco) throws Exception {
        try{
            ClienteEnderecoDAO clienteEnderecoDAO = new ClienteEnderecoDAO(connection);
            clienteEnderecoDAO.alterar(clienteEndereco);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    private void alteraCliente(Cliente cliente) throws Exception {
        try{
            ClienteDAO clienteDAO = new ClienteDAO(connection);
            clienteDAO.alterar(cliente);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    public void excluir(EntidadeDominio entidade, StringBuilder sb) {

    }

    public List<EntidadeDominio> consultar(EntidadeDominio entidade) throws Exception {
        try{
            if(connection == null || connection.isClosed()){
                connection = Conexao.getConnectionMySQL();
            }
            List<EntidadeDominio> entidadesSalvas = new ArrayList<>();
            switch (entidade) {
                case Cliente cliente -> entidadesSalvas = consultaCliente(cliente);
                case ClienteEndereco clienteEndereco -> entidadesSalvas = consultaClienteEndereco(clienteEndereco);
                case Bandeira bandeira -> entidadesSalvas = consultaBandeira(bandeira);
                case Cartao cartao -> entidadesSalvas = consultaCartao(cartao);
//                    }
//                    case Transacao transacao -> {
//                    }
//                    case Log log -> {
//                    }
                case null, default ->
                        throw new IllegalArgumentException("Tipo de entidade não suportado: " + entidade);
            }
            return entidadesSalvas;
        }catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage() + " " + entidade, e);
            throw new Exception(e.getMessage() + " " + entidade, e);
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqlEx) {
                    logger.log(Level.SEVERE, "Erro ao tentar fechar a conexão: " + sqlEx.getMessage(), sqlEx);
                }
            }
        }
    }

    private List<EntidadeDominio> consultaCartao(Cartao cartao) throws Exception {
        IDAO cartaoDAO = new CartaoDAO(connection);
        return cartaoDAO.consultar(cartao);
    }

    private List<EntidadeDominio> consultaBandeira(Bandeira bandeira) throws Exception {
        IDAO bandeiraDAO = new BandeiraDAO(connection);
        return bandeiraDAO.consultar(bandeira);
    }

    private List<EntidadeDominio> consultaClienteEndereco(ClienteEndereco clienteEndereco) throws Exception {
        IDAO clienteEnderecoDAO = new ClienteEnderecoDAO(connection);
        return clienteEnderecoDAO.consultar(clienteEndereco);
    }

    private List<EntidadeDominio> consultaCliente(Cliente cliente) throws Exception {
        IDAO clienteDAO = new ClienteDAO(connection);
        return clienteDAO.consultar(cliente);
    }
}
