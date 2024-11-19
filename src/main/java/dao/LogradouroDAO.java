package dao;

import dominio.EntidadeDominio;
import dominio.Logradouro;
import dominio.TipoLogradouro;
import util.Conexao;

import java.sql.*;
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
            logradouro.setTpLogradouro(salvaTipoLogradouro(logradouro, tipoLogradouroDAO));
            logger.log(Level.INFO, "tipo logradouro salvo: " + logradouro.getTpLogradouro().getTpLogradouro());
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
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) {
        return List.of();
    }
}
