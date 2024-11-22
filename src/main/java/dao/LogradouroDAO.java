package dao;

import dominio.EntidadeDominio;
import dominio.Logradouro;
import dominio.TipoLogradouro;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogradouroDAO implements IDAO{
    private Connection connection;
    private static final Logger logger = Logger.getLogger(LogradouroDAO.class.getName());

    public LogradouroDAO(Connection connection){
        this.connection = connection;
    }

    @Override
    public EntidadeDominio salvar(EntidadeDominio entidade) throws Exception {
        Logradouro logradouro = (Logradouro) entidade;
        StringBuilder sql = new StringBuilder();

        sql.append("INSERT INTO logradouro(lgr_logradouro, lgr_tpl_id, lgr_dt_cadastro) ");
        sql.append("VALUES (?,?,?)");

        try{
            if(connection == null){
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            IDAO tipoLogradouroDAO = new TipoLogradouroDAO(connection);
            List<EntidadeDominio> tipos = tipoLogradouroDAO.consultar(logradouro.getTpLogradouro());

            if(tipos.isEmpty()) {
                logradouro.setTpLogradouro(salvaTipoLogradouro(logradouro, tipoLogradouroDAO));
                logger.log(Level.INFO, "tipo logradouro salvo: " + logradouro.getTpLogradouro().getTpLogradouro());
            }
            else{
                logradouro.setTpLogradouro((TipoLogradouro) tipos.getFirst());
            }
            logradouro.complementarDtCadastro();

            logger.log(Level.INFO, "salvando logradouro " + logradouro.getLogradouro());
            try(PreparedStatement pst = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, logradouro.getLogradouro());
                pst.setInt(2, logradouro.getTpLogradouro().getId());
                pst.setTimestamp(3, new Timestamp(logradouro.getDtCadastro().getTime()));
                pst.executeUpdate();

                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idLogradouro = rs.getInt(1);
                        logradouro.setId(idLogradouro);
                    }
                }
                return logradouro;
            }
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar logradouro" + e.getMessage() + " " + logradouro, e);
            throw new Exception("Erro ao salvar o logradouro: " + e.getMessage() + " " + logradouro, e); // Lançar exceção em vez de retornar null
        }
    }

    private TipoLogradouro salvaTipoLogradouro(Logradouro logradouro, IDAO tipoLogradouroDAO) throws Exception {
        try {
            TipoLogradouro tpLogradouro = (TipoLogradouro) tipoLogradouroDAO.salvar(logradouro.getTpLogradouro());
            if (tpLogradouro != null) {
                return tpLogradouro;
            } else {
                throw new Exception("Falha ao salvar o tipo logradouro: o tipo logradouro retornado é nulo.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar tipo logradouro: " + e.getMessage(),e);
            throw new Exception("Erro ao salvar o tipo logradouro logradouro: " + e.getMessage(), e);
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
        Logradouro lgr = (Logradouro) entidade;
        try{
            List<EntidadeDominio> logradouros = new ArrayList<>();
            List<Object> parametros = new ArrayList<>();

            StringBuilder sql = new StringBuilder();
            sql.append("select * from crud_v2.logradouro l ");
            sql.append("inner join crud_v2.tipo_logradouro tl on l.lgr_tpl_id = tl.tpl_id ");
            sql.append("where 1=1 ");

            if(lgr.getId()!= null){
                sql.append(" and l.lgr_id = ? ");
                parametros.add(lgr.getId());
            }
            if(isStringValida(lgr.getLogradouro())){
                sql.append(" and l.lgr_logradouro = ? ");
                parametros.add(lgr.getLogradouro());
            }
            if(lgr.getTpLogradouro() != null){
                TipoLogradouro tpl = lgr.getTpLogradouro();
                if(tpl.getId() != null){
                    sql.append(" and tl.tpl_id = ? ");
                    parametros.add(tpl.getId());
                }
                if(isStringValida(tpl.getTpLogradouro())){
                    sql.append(" and tl.tpl_tipo = ? ");
                    parametros.add(tpl.getTpLogradouro());
                }
            }

            try(PreparedStatement pst = connection.prepareStatement(sql.toString())){
                for(int i = 0; i < parametros.size(); i++) {
                    pst.setObject(i + 1, parametros.get(i));
                }
                try(ResultSet rs = pst.executeQuery()){
                    while(rs.next()) {
                        Logradouro logradouro = new Logradouro();
                        logradouro.setId(rs.getInt("lgr_id"));
                        logradouro.setLogradouro(rs.getString("lgr_logradouro"));

                        TipoLogradouro tpLogradouro = new TipoLogradouro();
                        tpLogradouro.setId(rs.getInt("tpl_id"));
                        tpLogradouro.setTpLogradouro(rs.getString("tpl_tipo"));

                        logradouro.setTpLogradouro(tpLogradouro);

                        logradouros.add(logradouro);
                    }
                }
            }
            return logradouros;
        } catch (Exception e) {
            throw new Exception("Erro ao consultar logradouro: " + e.getMessage(), e);
        }
    }

    private boolean isStringValida(String value) {
        return value != null && !value.isBlank();
    }
}
