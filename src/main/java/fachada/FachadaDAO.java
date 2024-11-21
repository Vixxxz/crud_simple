package fachada;

import dao.ClienteDAO;
import dao.ClienteEnderecoDAO;
import dao.IDAO;
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
                    case Cliente cliente -> {
                        Cliente clienteSalvo = (Cliente) procuraOuSalvaCliente(cliente, connection);
                        logger.log(Level.INFO, "cliente salvo " + clienteSalvo.getCpf());
                        entidades.stream()
                                .filter(e -> e instanceof ClienteEndereco)
                                .forEach(e -> ((ClienteEndereco) e).setCliente(clienteSalvo));
                        entidadesSalvas.add(clienteSalvo);
                    }
                    case ClienteEndereco clienteEndereco -> {
                        clienteEndereco = (ClienteEndereco) procuraOuSalvaClienteEndereco(clienteEndereco, connection);
                        logger.log(Level.INFO, "cliente endereco salvo: " + clienteEndereco.getNumero());
                        entidadesSalvas.add(clienteEndereco);
                    }
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

    private EntidadeDominio procuraOuSalvaClienteEndereco(ClienteEndereco clienteEndereco, Connection connection) throws Exception {
        //todo: implementar a verificação se o cliente endereço ja existe antes de salvar
        IDAO clienteEnderecoDAO = new ClienteEnderecoDAO(connection);
        List<EntidadeDominio> clientesSalvos = clienteEnderecoDAO.consultar(clienteEndereco);
        if(clientesSalvos.isEmpty()){
            return clienteEnderecoDAO.salvar(clienteEndereco);
        }else{
            throw new Exception("Cliente Endereço já existente.");
        }
    }

    private EntidadeDominio procuraOuSalvaCliente(Cliente cliente, Connection connection) throws Exception {
        //todo: implementar a verificação se o cliente ja existe antes de salvar
        try{
            IDAO clienteDAO = new ClienteDAO(connection);
            List<EntidadeDominio> clientesSalvos = clienteDAO.consultar(cliente);
            if(clientesSalvos.isEmpty()){
                return clienteDAO.salvar(cliente);
            }else{
                throw new Exception("Cliente já existente.");
            }
        }catch(Exception e){
            throw new Exception(e.getMessage(), e);
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
