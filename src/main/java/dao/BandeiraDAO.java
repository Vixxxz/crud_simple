package dao;

import dominio.Bandeira;
import dominio.Cartao;
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

public class BandeiraDAO implements IDAO{
    private Connection connection;
    private static final Logger logger = Logger.getLogger(BandeiraDAO.class.getName());

    public BandeiraDAO (Connection connection){
        this.connection = connection;
    }

    public BandeiraDAO(){}

    @Override
    public EntidadeDominio salvar(EntidadeDominio entidade) throws Exception {
        Bandeira bandeira = (Bandeira) entidade;
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO bandeira(ban_bandeira, ban_dt_cadastro) VALUES (?,?)");

        try{
            if(connection == null || connection.isClosed()) {
                connection = Conexao.getConnectionMySQL();
            }

            try(PreparedStatement pst = connection.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS)){
                pst.setString(1, bandeira.getNomeBandeira());
                pst.setTimestamp(2, new java.sql.Timestamp(bandeira.getDtCadastro().getTime()));

                pst.executeUpdate();

                try(ResultSet rs = pst.getGeneratedKeys()){
                    if(rs.next()){
                        int idBandeira = rs.getInt(1);
                        bandeira.setId(idBandeira);
                    }
                }
            }
            return bandeira;
        } catch(Exception e){
            try{
                connection.rollback();
            }catch(SQLException rollbackEx){
                logger.log(Level.SEVERE, "Erro ao tentar realizar o rollback " + rollbackEx.getMessage(), rollbackEx);
            }
            logger.log(Level.SEVERE, "Erro ao salvar bandeira: " + e.getMessage() + " " + bandeira, e);
            throw new Exception("Erro ao salvar a bandeira: " + e.getMessage() + " " + bandeira, e); // Lançar exceção em vez de retornar null
        }
    }

    @Override
    public EntidadeDominio alterar(EntidadeDominio entidade) throws Exception {
        Bandeira bandeira = (Bandeira) entidade;
        try{
            if (connection == null) {
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE crud_v2.bandeira SET ");
            sql.append("ban_bandeira = ?, ban_dt_cadastro = ? ");
            sql.append("WHERE ban_id = ?");

            try(PreparedStatement pst = connection.prepareStatement(sql.toString())){
                pst.setString(1, bandeira.getNomeBandeira());
                pst.setTimestamp(2, new java.sql.Timestamp(bandeira.getDtCadastro().getTime()));
                pst.setInt(3, bandeira.getId());

                int rowsUpdated = pst.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new Exception("Nenhuma bandeira encontrado com o ID: " + bandeira.getId());
                }
            }
            connection.commit();
            return bandeira;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao atualizar bandeira: " + e.getMessage(), e);
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    logger.log(Level.SEVERE, "Erro ao realizar rollback: " + rollbackEx.getMessage(), rollbackEx);
                }
            }
            throw new Exception("Erro ao atualizar a bandeira: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(EntidadeDominio entidade) throws Exception {
        try{
            if (connection == null) {
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            Bandeira bandeira = (Bandeira) entidade;
            List<EntidadeDominio>bandeiras = consultar(bandeira);
            if(bandeiras.isEmpty()) {
                throw new Exception("Nenhuma bandeira encontrada com o ID: " + bandeira.getId());
            }

            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM crud_v2.bandeira WHERE ban_id = ?");

            CartaoDAO cartaoDAO = new CartaoDAO();
            Cartao cartao = new Cartao();
            cartao.setBandeira(bandeira);
            cartaoDAO.excluir(cartao);

            try(PreparedStatement pst = connection.prepareStatement(sql.toString())){
                pst.setInt(1, bandeira.getId());
                pst.executeUpdate();
            }
            connection.commit();
            logger.log(Level.INFO, "Bandeira e cartoes excluidos com sucesso");
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao excluir bandeira: " + e.getMessage(), e);
            throw new Exception("Erro ao excluir a bandeira: " + e.getMessage(), e);
        }
    }

    @Override
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) throws Exception {
        Bandeira bandeira = (Bandeira) entidade;
        try {
            List<EntidadeDominio> bandeiras = new ArrayList<>();
            List<Object> parametros = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM crud_v2.bandeira b WHERE 1=1");

            if(bandeira.getId()!= null){
                sql.append(" AND b.ban_id = ? ");
                parametros.add(bandeira.getId());
            }

            if(bandeira.getNomeBandeira()!= null &&!bandeira.getNomeBandeira().isBlank()){
                sql.append(" AND b.ban_bandeira = ? ");
                parametros.add(bandeira.getNomeBandeira());
            }
            try(PreparedStatement pst = connection.prepareStatement(sql.toString())){
                for(int i = 0; i < parametros.size(); i++) {
                    pst.setObject(i+1, parametros.get(i));
                }

                try(ResultSet rs = pst.executeQuery()){
                    while(rs.next()) {
                        Bandeira ban = new Bandeira();
                        ban.setId(rs.getInt("ban_id"));
                        ban.setNomeBandeira(rs.getString("ban_bandeira"));
                        bandeiras.add(ban);
                    }
                }
            }
            return bandeiras;
        }catch (Exception e) {
            throw new Exception("Erro ao consultar a bandeira: " + e.getMessage(), e);
        }
    }
}
