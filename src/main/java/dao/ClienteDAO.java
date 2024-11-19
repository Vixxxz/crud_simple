package dao;

import dominio.Cliente;
import dominio.EntidadeDominio;
import util.Conexao;

import java.sql.*;
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
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) {
        return List.of();
    }
}
