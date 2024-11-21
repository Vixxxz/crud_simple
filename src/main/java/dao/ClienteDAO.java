package dao;

import dominio.Cliente;
import dominio.EntidadeDominio;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteDAO implements IDAO{
    private Connection connection;
    private static final Logger logger = Logger.getLogger(ClienteDAO.class.getName());

    public ClienteDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public EntidadeDominio salvar(EntidadeDominio entidade) throws Exception {
        Cliente cliente = (Cliente) entidade;
        try{
            if (connection == null) {
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO cliente(cli_cpf, cli_email, cli_senha, cli_nome, ");
            sql.append("cli_genero, cli_dt_nasc, cli_tp_tel, cli_tel, cli_ranking, cli_dt_cadastro) ");
            sql.append("VALUES (?,?,?,?,?,?,?,?,?,?)");

            logger.log(Level.INFO, "salvando cliente: " + cliente.getCpf());
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
                pst.executeUpdate();

                logger.log(Level.INFO, "pegando id do cliente salvo");
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idCliente = rs.getInt(1);
                        cliente.setId(idCliente);
                    }
                }
                logger.log(Level.INFO, "cliente com id: " + cliente.getId());
                return cliente;
            }
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar cliente: " + e.getMessage() + " " + cliente, e);
            throw new Exception("Erro ao salvar o cliente: " + e.getMessage() + " " + cliente, e);
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
        List<EntidadeDominio> clientes = new ArrayList<>();
        try{
            Cliente cliente = (Cliente) entidade;
            List<Object> parametros = new ArrayList<>();

            StringBuilder sql = new StringBuilder();
            sql.append("select * from crud_v2.cliente ");
            sql.append("where 1=1");


            if(cliente.getId() != null){
                sql.append("and c.cli_id = ? ");
                parametros.add(cliente.getId());
            }
            if(isValidString(cliente.getRanking())){
                sql.append("and c.cli_rank = ? ");
                parametros.add(cliente.getRanking());
            }
            if(isValidString(cliente.getNome())){
                sql.append("and c.cli_nome = ? ");
                parametros.add(cliente.getNome());
            }
            if(isValidString(cliente.getGenero())){
                sql.append("and c.cli_genero = ? ");
                parametros.add(cliente.getGenero());
            }
            if(isValidString(cliente.getCpf())){
                sql.append("and c.cli_cpf = ? ");
                parametros.add(cliente.getCpf());
            }
            if(isValidString(cliente.getTipoTelefone())){
                sql.append("and c.cli_tp_tel = ? ");
                parametros.add(cliente.getTipoTelefone());
            }
            if(isValidString(cliente.getTelefone())){
                sql.append("and c.cli_tel = ? ");
                parametros.add(cliente.getTelefone());
            }
            if(isValidString(cliente.getEmail())){
                sql.append("and c.cli_email = ? ");
                parametros.add(cliente.getEmail());
            }
            if(isValidString(cliente.getSenha())){
                sql.append("and c.cli_senha = ? ");
                parametros.add(cliente.getSenha());
            }
            if(cliente.getDataNascimento() != null){
                sql.append("and c.cli_dt_nasc = ? ");
                parametros.add(cliente.getDataNascimento());
            }
            try (PreparedStatement pst = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < parametros.size(); i++) {
                    pst.setObject(i + 1, parametros.get(i));
                }

                try(ResultSet rs = pst.executeQuery()){
                    while (rs.next()) {
                        Cliente cli = new Cliente();
                        cli.setId(rs.getInt("cli_id"));
                        cli.setRanking(rs.getString("cli_rank"));
                        cli.setNome(rs.getString("cli_nome"));
                        cli.setGenero(rs.getString("cli_genero"));
                        cli.setCpf(rs.getString("cli_cpf"));
                        cli.setTipoTelefone(rs.getString("cli_tp_tel"));
                        cli.setTelefone(rs.getString("cli_tel"));
                        cli.setEmail(rs.getString("cli_email"));
                        cli.setSenha(rs.getString("cli_senha"));
                        cli.setDataNascimento(rs.getDate("cli_dt_nasc"));
                        clientes.add(cli);
                    }                }
            }
            return clientes;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar cliente: " + e.getMessage() + " " + clientes, e);
            throw new Exception("Erro ao salvar o cliente: " + e.getMessage() + " " + clientes, e);
        }
    }

    private boolean isValidString(String value) {
        return value != null && !value.isBlank();
    }
}
