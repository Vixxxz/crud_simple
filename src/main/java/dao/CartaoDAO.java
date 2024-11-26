package dao;

import dominio.Bandeira;
import dominio.Cartao;
import dominio.Cliente;
import dominio.EntidadeDominio;
import util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CartaoDAO implements IDAO{
    private Connection connection;
    private static final Logger logger = Logger.getLogger(CartaoDAO.class.getName());

    public CartaoDAO (Connection connection){
        this.connection = connection;
    }

    public CartaoDAO (){}

    @Override
    public EntidadeDominio salvar(EntidadeDominio entidade) throws Exception {
        Cartao cartao = (Cartao) entidade;
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO cartao(car_num, car_num_seguranca, car_nome_impresso, car_preferencial, ban_dt_cadastro) ");
        sql.append("VALUES (?,?,?,?,?)");
        try{
            if(connection == null || connection.isClosed()) {
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            //todo: buscar a bandeira e o cliente antes de salvar o cartao

            try(PreparedStatement pst = connection.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS)){
                pst.setString(1, cartao.getNumero());
                pst.setString(2, cartao.getNumSeguranca());
                pst.setString(3, cartao.getNomeImpresso());
                pst.setBoolean(4, cartao.getPreferencial());
                pst.setTimestamp(5, new java.sql.Timestamp(cartao.getDtCadastro().getTime()));

                pst.executeUpdate();

                try(ResultSet rs = pst.getGeneratedKeys()){
                    if(rs.next()){
                        int idCartao = rs.getInt(1);
                        cartao.setId(idCartao);
                    }
                }
            }
            return cartao;
        } catch(Exception e){
            try{
                connection.rollback();
            }catch(SQLException rollbackEx){
                logger.log(Level.SEVERE, "Erro ao tentar realizar o rollback " + rollbackEx.getMessage(), rollbackEx);
            }
            logger.log(Level.SEVERE, "Erro ao salvar cartao: " + e.getMessage() + " " + cartao, e);
            throw new Exception("Erro ao salvar o cartao: " + e.getMessage() + " " + cartao, e); // Lançar exceção em vez de retornar null
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
        Cartao cartao = (Cartao) entidade;
        List<EntidadeDominio> cartoes = new ArrayList<>();
        List<Object> parametros = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT * FROM crud_v2.cartao c ")
                .append("INNER JOIN crud_v2.bandeira b ON c.car_ban_id = b.ban_id ")
                .append("INNER JOIN crud_v2.cliente cl ON c.car_cli_id = cl.cli_id ")
                .append("WHERE 1=1 ");

        try {
            adicionarCondicoesCartao(cartao, sql, parametros);
            adicionarCondicoesCliente(cartao.getCliente(), sql, parametros);
            adicionarCondicoesBandeira(cartao.getBandeira(), sql, parametros);

            try (PreparedStatement pst = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < parametros.size(); i++) {
                    pst.setObject(i + 1, parametros.get(i));
                }

                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        cartoes.add(mapearResultado(rs));
                    }
                }
            }
            return cartoes;

        } catch (Exception e) {
            throw new Exception("Erro ao consultar o cartão: " + e.getMessage(), e);
        }
    }

    private void adicionarCondicoesCartao(Cartao cartao, StringBuilder sql, List<Object> parametros) {
        if (cartao.getId() != null) {
            sql.append(" AND c.car_id = ? ");
            parametros.add(cartao.getId());
        }
        if (isStringValida(cartao.getNumero())) {
            sql.append(" AND c.car_num = ? ");
            parametros.add(cartao.getNumero());
        }
        if (isStringValida(cartao.getNumSeguranca())) {
            sql.append(" AND c.car_num_seguranca = ? ");
            parametros.add(cartao.getNumSeguranca());
        }
        if (isStringValida(cartao.getNomeImpresso())) {
            sql.append(" AND c.car_nome_impresso = ? ");
            parametros.add(cartao.getNomeImpresso());
        }
        if (cartao.getPreferencial() != null) {
            sql.append(" AND c.car_preferencial = ? ");
            parametros.add(cartao.getPreferencial());
        }
    }

    private void adicionarCondicoesCliente(Cliente cliente, StringBuilder sql, List<Object> parametros) {
        if (cliente != null) {
            if (cliente.getId() != null) {
                sql.append(" AND cl.cli_id = ? ");
                parametros.add(cliente.getId());
            }
            if (isStringValida(cliente.getRanking())) {
                sql.append(" AND cl.cli_ranking = ? ");
                parametros.add(cliente.getRanking());
            }
            if (isStringValida(cliente.getNome())) {
                sql.append(" AND cl.cli_nome = ? ");
                parametros.add(cliente.getNome());
            }
            if (isStringValida(cliente.getGenero())) {
                sql.append(" AND cl.cli_genero = ? ");
                parametros.add(cliente.getGenero());
            }
            if (isStringValida(cliente.getCpf())) {
                sql.append(" AND cl.cli_cpf = ? ");
                parametros.add(cliente.getCpf());
            }
            if (isStringValida(cliente.getTipoTelefone())) {
                sql.append(" AND cl.cli_tp_tel = ? ");
                parametros.add(cliente.getTipoTelefone());
            }
            if (isStringValida(cliente.getTelefone())) {
                sql.append(" AND cl.cli_tel = ? ");
                parametros.add(cliente.getTelefone());
            }
            if (isStringValida(cliente.getEmail())) {
                sql.append(" AND cl.cli_email = ? ");
                parametros.add(cliente.getEmail());
            }
            if (isStringValida(cliente.getSenha())) {
                sql.append(" AND cl.cli_senha = ? ");
                parametros.add(cliente.getSenha());
            }
            if (cliente.getDataNascimento() != null) {
                sql.append(" AND cl.cli_dt_nasc = ? ");
                parametros.add(cliente.getDataNascimento());
            }
        }
    }

    private void adicionarCondicoesBandeira(Bandeira bandeira, StringBuilder sql, List<Object> parametros) {
        if (bandeira != null) {
            if (bandeira.getId() != null) {
                sql.append(" AND b.ban_id = ? ");
                parametros.add(bandeira.getId());
            }
            if (isStringValida(bandeira.getNomeBandeira())) {
                sql.append(" AND b.ban_bandeira = ? ");
                parametros.add(bandeira.getNomeBandeira());
            }
        }
    }

    private Cartao mapearResultado(ResultSet rs) throws SQLException {
        Bandeira ban = new Bandeira();
        ban.setId(rs.getInt("ban_id"));
        ban.setNomeBandeira(rs.getString("ban_bandeira"));

        Cliente cli = new Cliente();
        cli.setId(rs.getInt("cli_id"));
        cli.setRanking(rs.getString("cli_ranking"));
        cli.setNome(rs.getString("cli_nome"));
        cli.setGenero(rs.getString("cli_genero"));
        cli.setCpf(rs.getString("cli_cpf"));
        cli.setTipoTelefone(rs.getString("cli_tp_tel"));
        cli.setTelefone(rs.getString("cli_tel"));
        cli.setEmail(rs.getString("cli_email"));
        cli.setSenha(rs.getString("cli_senha"));
        cli.setDataNascimento(rs.getDate("cli_dt_nasc"));

        Cartao car = new Cartao();
        car.setId(rs.getInt("car_id"));
        car.setNumero(rs.getString("car_num"));
        car.setNumSeguranca(rs.getString("car_num_seguranca"));
        car.setNomeImpresso(rs.getString("car_nome_impresso"));
        car.setPreferencial(rs.getBoolean("car_preferencial"));
        car.setBandeira(ban);
        car.setCliente(cli);

        return car;
    }

    private boolean isStringValida(String value) {
        return value != null && !value.isBlank();
    }
}
