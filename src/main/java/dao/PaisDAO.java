package dao;

import dominio.EntidadeDominio;
import dominio.Pais;
import util.Conexao;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaisDAO implements IDAO{
    private Connection connection;
    private boolean ctrlTransaction = true;
    private static final Logger logger = Logger.getLogger(PaisDAO.class.getName());

    public PaisDAO(Connection connection){
        this.connection = connection;
    }

    @Override
    public EntidadeDominio salvar(EntidadeDominio entidade) throws Exception {
        Pais pais = (Pais) entidade;
        StringBuilder sql = new StringBuilder();

        sql.append("INSERT INTO pais(pai_pais, pai_dt_cadastro) VALUES (?, ?)");

        try{
            if(connection == null){
                connection = Conexao.getConnectionMySQL();
            }else{
                ctrlTransaction = false;
            }
            connection.setAutoCommit(false);

            pais.complementarDtCadastro();

            try (PreparedStatement pst = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, pais.getPais());
                pst.setTimestamp(2, new Timestamp(pais.getDtCadastro().getTime()));
                pst.executeUpdate();

                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idPais = rs.getInt(1);
                        pais.setId(idPais);
                    }
                }
                connection.commit();
                return pais;
            }
        }catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Erro ao tentar realizar o rollback" + rollbackEx.getMessage(), rollbackEx);
            }
            logger.log(Level.SEVERE, "Erro ao salvar pais: " + e.getMessage() + " " + pais, e);
            throw new Exception("Erro ao salvar o pais: " + e.getMessage() + " " + pais, e); // Lançar exceção em vez de retornar null
        } finally {
            if (ctrlTransaction && connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqlEx) {
                    logger.log(Level.SEVERE, "Erro ao tentar fechar a conexão" + sqlEx.getMessage(), sqlEx);
                }
            }
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
