package dao;

import dominio.EntidadeDominio;
import dominio.TipoLogradouro;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TipoLogradouroDAO implements IDAO{
    private Connection connection;
    private static final Logger logger = Logger.getLogger(TipoLogradouroDAO.class.getName());

    public TipoLogradouroDAO(Connection connection){
        this.connection = connection;
    }

    @Override
    public EntidadeDominio salvar(EntidadeDominio entidade) throws Exception {
        TipoLogradouro tpLogradouro = (TipoLogradouro) entidade;
        StringBuilder sql = new StringBuilder();

        sql.append("INSERT INTO tipo_logradouro(tpl_tipo, tpl_dt_cadastro) ");
        sql.append("VALUES (?,?)");

        try{
            if(connection == null){
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            tpLogradouro.complementarDtCadastro();

            logger.log(Level.INFO, "salvando tipo logradouro: " + tpLogradouro.getTpLogradouro());
            try(PreparedStatement pst = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)){
                pst.setString(1, tpLogradouro.getTpLogradouro());
                pst.setTimestamp(2, new Timestamp(tpLogradouro.getDtCadastro().getTime()));
                pst.executeUpdate();

                try(ResultSet rs = pst.getGeneratedKeys()){
                    if(rs.next()){
                        int idTpLgr = rs.getInt(1);
                        tpLogradouro.setId(idTpLgr);
                    }
                }
                return tpLogradouro;
            }
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar tipo logradouro: " + e.getMessage() + " " + tpLogradouro, e);
            throw new Exception("Erro ao salvar o tipo logradouro: " + e.getMessage() + " " + tpLogradouro, e); // Lançar exceção em vez de retornar null
        }
    }

    @Override
    public EntidadeDominio alterar(EntidadeDominio entidade) throws Exception {
        TipoLogradouro tpl = (TipoLogradouro) entidade;
        StringBuilder sql = new StringBuilder();

        sql.append("UPDATE crud_v2.tipo_logradouro SET ");
        sql.append("tpl_tipo = ?, tpl_dt_cadastro = ? ");
        sql.append("WHERE tpl_id = ?");

        try{
            if(connection == null || connection.isClosed()){
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            tpl.complementarDtCadastro();

            logger.log(Level.INFO, "alterando tipo logradouro: " + tpl.getTpLogradouro());
            try(PreparedStatement pst = connection.prepareStatement(sql.toString())){
                pst.setString(1, tpl.getTpLogradouro());
                pst.setTimestamp(2, new Timestamp(tpl.getDtCadastro().getTime()));
                pst.setInt(3, tpl.getId());

                int rowsUpdated = pst.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new Exception("Nenhum tipo logradouro encontrado para o ID " + tpl.getId());
                }
            }
            connection.commit();
            return tpl;
        }catch (SQLException e) {
            throw new Exception("Erro ao atualizar tipo logradouro: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(EntidadeDominio entidade) {

    }

    @Override
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) throws Exception {
        TipoLogradouro tpl = (TipoLogradouro) entidade;
        try{
            List<EntidadeDominio> tipos = new ArrayList<>();
            List<Object> parametros = new ArrayList<>();

            StringBuilder sql = new StringBuilder();
            sql.append("select * from crud_v2.tipo_logradouro tl ");
            sql.append("where 1=1 ");

            if(tpl.getId() != null){
                sql.append("and tl.tpl_id = ? ");
                parametros.add(tpl.getId());
            }
            if(tpl.getTpLogradouro() != null){
                sql.append("and tl.tpl_tipo = ? ");
                parametros.add(tpl.getTpLogradouro());
            }

            try(PreparedStatement pst = connection.prepareStatement(sql.toString())){
                for(int i = 0; i < parametros.size(); i++){
                    pst.setObject(i+1, parametros.get(i));
                }
                try(ResultSet rs = pst.executeQuery()){
                    while(rs.next()){
                        TipoLogradouro tpLogradouro = new TipoLogradouro();
                        tpLogradouro.setId(rs.getInt("tpl_id"));
                        tpLogradouro.setTpLogradouro(rs.getString("tpl_tipo"));
                        tipos.add(tpLogradouro);
                    }
                }
            }
            return tipos;
        } catch (Exception e) {
            throw new Exception("Erro ao consultar tipo logradouro: " + e.getMessage(), e);
        }
    }
}
