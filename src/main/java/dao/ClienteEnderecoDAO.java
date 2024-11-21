package dao;

import dominio.ClienteEndereco;
import dominio.Endereco;
import dominio.EntidadeDominio;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteEnderecoDAO implements IDAO{
    private Connection connection;
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
            }
            connection.setAutoCommit(false);

            IDAO enderecoDAO = new EnderecoDAO(connection);
            clienteEndereco.setEndereco(salvaEndereco(clienteEndereco, enderecoDAO));
            logger.log(Level.INFO, "endereco salvo: " + clienteEndereco.getEndereco().getCep());
            clienteEndereco.complementarDtCadastro();

            logger.log(Level.INFO, "salvando cliente endereco: " + clienteEndereco.getNumero());
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
                return clienteEndereco;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao salvar ClienteEndereco: " + e.getMessage() + " " + clienteEndereco, e);
            throw new Exception("Erro ao salvar ClienteEndereco: " + e.getMessage() + " " + clienteEndereco, e);
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
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) throws Exception {
        ClienteEndereco clienteEndereco = (ClienteEndereco) entidade;
        try{
            List<EntidadeDominio> clientesEnderecos = new ArrayList<>();
            List<Object> parametros = new ArrayList<>();

            StringBuilder sql = new StringBuilder();
            sql.append("select * from crud_v2.cliente_endereco ce ");
            sql.append("inner join crud_v2.cliente c on c.cli_id = ce.cli_end_cli_id ");
            sql.append("inner join crud_v2.endereco e on e.end_id = ce.cli_end_end_id ");
            sql.append("where 1=1");

            if(clienteEndereco.getId() != null){
                sql.append("and ce.cli_end_id = ? ");
                parametros.add(clienteEndereco.getId());
            }
            if(isValidString(clienteEndereco.getNumero())){
                sql.append("and ce.cli_end_num = ? ");
                parametros.add(clienteEndereco.getNumero());
            }
            if(isValidString(clienteEndereco.getTipoEndereco())){
                sql.append("and ce.cli_end_tp_end = ? ");
                parametros.add(clienteEndereco.getTipoEndereco());
            }
            if(isValidString(clienteEndereco.getTipoResidencia())){
                sql.append("and ce.cli_end_tp_residencia = ? ");
                parametros.add(clienteEndereco.getTipoResidencia());
            }
            if(isValidString(clienteEndereco.getObservacoes())){
                sql.append("and ce.cli_end_obs = ? ");
                parametros.add(clienteEndereco.getObservacoes());
            }
            if(clienteEndereco.getCliente() != null){
                if(clienteEndereco.getCliente().getId() != null){
                    sql.append("and c.cli_id = ? ");
                    parametros.add(clienteEndereco.getCliente().getId());
                }
                if(isValidString(clienteEndereco.getCliente().getRanking())){
                    sql.append("and c.cli_rank = ? ");
                    parametros.add(clienteEndereco.getCliente().getRanking());
                }
                if(isValidString(clienteEndereco.getCliente().getNome())){
                    sql.append("and c.cli_nome = ? ");
                    parametros.add(clienteEndereco.getCliente().getNome());
                }
                if(isValidString(clienteEndereco.getCliente().getGenero())){
                    sql.append("and c.cli_genero = ? ");
                    parametros.add(clienteEndereco.getCliente().getGenero());
                }
                if(isValidString(clienteEndereco.getCliente().getCpf())){
                    sql.append("and c.cli_cpf = ? ");
                    parametros.add(clienteEndereco.getCliente().getCpf());
                }
                if(isValidString(clienteEndereco.getCliente().getTipoTelefone())){
                    sql.append("and c.cli_tp_tel = ? ");
                    parametros.add(clienteEndereco.getCliente().getTipoTelefone());
                }
                if(isValidString(clienteEndereco.getCliente().getTelefone())){
                    sql.append("and c.cli_tel = ? ");
                    parametros.add(clienteEndereco.getCliente().getTelefone());
                }
                if(isValidString(clienteEndereco.getCliente().getEmail())){
                    sql.append("and c.cli_email = ? ");
                    parametros.add(clienteEndereco.getCliente().getEmail());
                }
                if(isValidString(clienteEndereco.getCliente().getSenha())){
                    sql.append("and c.cli_senha = ? ");
                    parametros.add(clienteEndereco.getCliente().getSenha());
                }
                if(clienteEndereco.getCliente().getDataNascimento() != null){
                    sql.append("and c.cli_dt_nasc = ? ");
                    parametros.add(clienteEndereco.getCliente().getDataNascimento());
                }
            }
            if(clienteEndereco.getEndereco() != null){
                if(isValidString(clienteEndereco.getEndereco().getCep())){
                    sql.append("and e.end_cep = ? ");
                    parametros.add(clienteEndereco.getEndereco().getCep());
                }
                if(clienteEndereco.getEndereco().getBairro() != null && clienteEndereco.getEndereco().getBairro().getId() != null){
                    sql.append("and e.end_bai_id = ? ");
                    parametros.add(clienteEndereco.getEndereco().getBairro().getId());
                }
                if(clienteEndereco.getEndereco().getLogradouro() != null && clienteEndereco.getEndereco().getLogradouro().getId() != null){
                    sql.append("and e.end_log_id = ? ");
                    parametros.add(clienteEndereco.getEndereco().getLogradouro().getId());
                }
            }

            try(PreparedStatement pst = )

            return clientesEnderecos;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private boolean isValidString(String value) {
        return value != null && !value.isBlank();
    }
}