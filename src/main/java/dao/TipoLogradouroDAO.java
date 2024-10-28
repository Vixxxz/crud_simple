package dao;

import dominio.EntidadeDominio;
import dominio.TipoLogradouro;
import util.Conexao;

import java.sql.*;
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
