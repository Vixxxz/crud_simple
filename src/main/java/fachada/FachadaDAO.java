package fachada;

import dao.BandeiraDAO;
import dao.ClienteDAO;
import dao.ClienteEnderecoDAO;
import dao.IDAO;
import dominio.Bandeira;
import dominio.Cliente;
import dominio.ClienteEndereco;
import dominio.EntidadeDominio;
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

    public void alterar(EntidadeDominio entidade, StringBuilder sb) {

    }

    public void excluir(EntidadeDominio entidade, StringBuilder sb) {

    }

    public List<EntidadeDominio> consultar(EntidadeDominio entidade, StringBuilder sb) {
        return List.of();
    }
}
