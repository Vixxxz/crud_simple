package dao;

import dominio.Cliente;
import dominio.ClienteEndereco;
import dominio.EntidadeDominio;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteDAO implements IDAO{
    private Connection connection;
    private boolean ctrlTransaction = true;
    private static final Logger logger = Logger.getLogger(ClienteDAO.class.getName());

    @Override
    public EntidadeDominio salvar(EntidadeDominio entidade) throws Exception {
        Cliente cliente = (Cliente) entidade;
        try{
            connection = Conexao.getConnectionMySQL();
            connection.setAutoCommit(false);

            IDAO clienteEnderecoDAO = new ClienteEnderecoDAO(connection);

            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO cliente(cli_cpf, cli_email, cli_senha, cli_nome, ");
            sql.append("cli_genero, cli_dt_nasc, cli_tp_tel, cli_tel, cli_ranking, cli_dt_cadastro) ");
            sql.append("VALUES (?,?,?,?,?,?,?,?,?,?)");

            try (PreparedStatement pst = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, cliente.getCpf());
                pst.setString(2, cliente.getEmail());
                pst.setString(3, cliente.getSenha());
                pst.setString(4, cliente.getNome());
                pst.setString(5, cliente.getGenero());
                pst.setDate(6, new Date(cliente.getDataNascimento().getTime()));
                pst.setString(7, cliente.getTipoTelefone());
                pst.setString(8, cliente.getTelefone());
                pst.setString(9, cliente.getRanking());
                pst.setTimestamp(10, new Timestamp(cliente.getDtCadastro().getTime()));

                logger.log(Level.INFO, "salvando cliente no banco");
                pst.executeUpdate();

                logger.log(Level.INFO, "pegando id do cliente salvo");
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idCliente = rs.getInt(1);
                        cliente.setId(idCliente);
                    }
                }

                logger.log(Level.INFO, "cliente com id: " + cliente.getId() + cliente);
                try{
                    cliente.setEnderecosRelacionados(salvaClienteEndereco(cliente, clienteEnderecoDAO));
                } catch (Exception e) {
                    throw new Exception("Erro: " + e.getMessage(), e);
                }

                logger.log(Level.INFO, "cliente com enderecos relacionados salvo: " + cliente);
                connection.commit();
                return cliente;
            }
        }catch (Exception e) {
            try {
                connection.rollback();
                logger.log(Level.INFO, "Rollback realizado");
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Erro ao tentar realizar o rollback " + rollbackEx.getMessage(), rollbackEx);
            }
            logger.log(Level.SEVERE, "Erro ao salvar cliente " + e.getMessage() + " " + cliente, e);
            throw new Exception("Erro ao salvar o cliente: " + e.getMessage() + " " + cliente, e);
        } finally {
            if (ctrlTransaction && connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqlEx) {
                    logger.log(Level.SEVERE, "Erro ao tentar fechar a conexão" + sqlEx.getMessage(), sqlEx);
                }
            }
        }
    }

    private List<ClienteEndereco> salvaClienteEndereco(Cliente cliente, IDAO clienteEnderecoDAO) throws Exception {
        List<ClienteEndereco> clienteEnderecos = new ArrayList<>();

        try{
            cliente.getEnderecosRelacionados().forEach(clienteEnderecoTemp -> {
                clienteEnderecoTemp.setCliente(cliente);
                try {
                    ClienteEndereco clienteEndereco = (ClienteEndereco) clienteEnderecoDAO.salvar(clienteEnderecoTemp);
                    if (clienteEndereco != null) {
                        clienteEnderecos.add(clienteEndereco);
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE,    "Erro ao salvar Cliente Endereco: " +
                            clienteEnderecoTemp + " " + e.getMessage(), e);
                }
            });
            if(clienteEnderecos.isEmpty()){
                throw new Exception("a lista de cliente endereco é nula" + cliente);
            }
        } catch (Exception e) {
            throw new Exception("Erro ao salvar cliente endereco: " + e.getMessage() + " " + clienteEnderecos, e);
        }

        return clienteEnderecos;
    }

    @Override
    public void alterar(EntidadeDominio entidade) {

    }

    @Override
    public void excluir(EntidadeDominio entidade) {

    }

    @Override
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) {
        return List.of();
    }
}
