package dao;

import dominio.Bairro;
import dominio.Cidade;
import dominio.EntidadeDominio;
import util.Conexao;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BairroDAO implements IDAO{
    private Connection connection;
    private boolean ctrlTransaction = true;
    private static final Logger logger = Logger.getLogger(BairroDAO.class.getName());

    public BairroDAO(Connection connection){
        this.connection = connection;
    }

    @Override
    public EntidadeDominio salvar(EntidadeDominio entidade) throws Exception {
        Bairro bairro = (Bairro) entidade;
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO bairro(bai_bairro, bai_cid_id, bai_dt_cadastro) ");
        sql.append("VALUES (?,?,?)");

        try {
            if (connection == null) {
                connection = Conexao.getConnectionMySQL();
            } else {
                ctrlTransaction = false;
            }
            connection.setAutoCommit(false);

            IDAO cidadeDAO = new CidadeDAO(connection);
            bairro.setCidade(salvaCidade(bairro, cidadeDAO));
            bairro.complementarDtCadastro();

            try(PreparedStatement pst = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)){
                pst.setString(1, bairro.getBairro());
                pst.setInt(2, bairro.getCidade().getId());
                pst.setTimestamp(3, new Timestamp(bairro.getDtCadastro().getTime()));

                pst.executeUpdate();

                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idBairro = rs.getInt(1);
                        bairro.setId(idBairro);
                    }
                }
                connection.commit();
                return bairro;
            }
        }catch (Exception e) {
            try{
                connection.rollback();
            }catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Erro ao tentar realizar o rollback " + rollbackEx.getMessage(), rollbackEx);
            }
            logger.log(Level.SEVERE, "Erro ao salvar entidade: " + e.getMessage() + " " + bairro, e);
            throw new Exception("Erro ao salvar a entidade: " + e.getMessage() + " " + bairro, e); // Lançar exceção em vez de retornar null
        }finally {
            if(ctrlTransaction && connection!= null){
                try{
                    connection.close();
                }catch (SQLException sqlEx){
                    logger.log(Level.SEVERE, "Erro ao tentar fechar a conexão", sqlEx);
                }
            }
        }
    }

    private Cidade salvaCidade(Bairro bairro, IDAO cidadeDAO) throws Exception {
        try {
            Cidade cidade = (Cidade) cidadeDAO.salvar(bairro.getCidade());
            if (cidade != null) {
                return cidade;
            } else {
                throw new Exception("Falha ao salvar a cidade: a cidade retornada é nula.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar cidade: " + e.getMessage(), e);
            throw new Exception("Erro ao salvar a cidade." + e.getMessage(), e);
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
