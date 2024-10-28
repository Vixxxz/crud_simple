package dao;

import dominio.ClienteEndereco;
import dominio.Endereco;
import dominio.EntidadeDominio;
import util.Conexao;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteEnderecoDAO implements IDAO{
    private Connection connection;
    private boolean ctrlTransaction = true;
    private static final Logger logger = Logger.getLogger(ClienteEnderecoDAO.class.getName());

    public ClienteEnderecoDAO(Connection connection){
        this.connection = connection;
    }

    @Override
    public EntidadeDominio salvar(EntidadeDominio entidade) throws Exception {
        ClienteEndereco clienteEndereco = (ClienteEndereco) entidade;
        StringBuilder sql = new StringBuilder();

        sql.append("INSERT INTO cliente_endereco(cli_end_cli_id, cli_end_end_id, cli_end_num, ");
        sql.append("cli_end_tp_residencia, cli_end_tp_end, cli_end_obs, cli_end_dt_cadastro) ");
        sql.append("VALUES (?,?,?,?,?,?,?)");

        try {
            if (connection == null) {
                connection = Conexao.getConnectionMySQL();
            } else {
                ctrlTransaction = false;
            }
            connection.setAutoCommit(false);

            IDAO enderecoDAO = new EnderecoDAO(connection);
            clienteEndereco.setEndereco(salvaEndereco(clienteEndereco, enderecoDAO));
            clienteEndereco.complementarDtCadastro();

            try (PreparedStatement pst = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
                pst.setInt(1, clienteEndereco.getCliente().getId());
                pst.setInt(2, clienteEndereco.getEndereco().getId());
                pst.setString(3, clienteEndereco.getNumero());
                pst.setString(4, clienteEndereco.getTipoResidencia());
                pst.setString(5, clienteEndereco.getTipoEndereco());
                pst.setString(6, clienteEndereco.getObservacoes());
                pst.setTimestamp(7, new Timestamp(clienteEndereco.getDtCadastro().getTime()));

                pst.executeUpdate();

                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idClienteEndereco = rs.getInt(1);
                        clienteEndereco.setId(idClienteEndereco);
                    }
                }
                connection.commit();
                return clienteEndereco;
            }
        }catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Erro ao tentar realizar o rollback " + rollbackEx.getMessage(), rollbackEx);
            }
            logger.log(Level.SEVERE, "Erro ao salvar ClienteEndereco: " + e.getMessage() + " " + clienteEndereco, e);
            throw new Exception("Erro ao salvar a entidade: " + e.getMessage() + " " + clienteEndereco, e);
        } finally {
            if (ctrlTransaction && connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqlEx) {
                    logger.log(Level.SEVERE, "Erro ao tentar fechar a conexão", sqlEx);
                }
            }
        }
    }

    private Endereco salvaEndereco(ClienteEndereco clienteEndereco, IDAO enderecoDAO) throws Exception {
        try {
            Endereco endereco = (Endereco) enderecoDAO.salvar(clienteEndereco.getEndereco());
            if (endereco != null) {
                return endereco;
            } else {
                throw new Exception("Falha ao salvar o endereço: o endereço retornado é nulo.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar endereco: " + e.getMessage(), e);
            throw new Exception("Erro ao salvar o endereço: " + e.getMessage(), e);
        }
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
