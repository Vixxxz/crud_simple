package dao;

import dominio.Cidade;
import dominio.EntidadeDominio;
import dominio.Uf;
import util.Conexao;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CidadeDAO implements IDAO{
    private Connection connection;
    private static final Logger logger = Logger.getLogger(CidadeDAO.class.getName());

    public CidadeDAO(Connection connection){
        this.connection = connection;
    }

    @Override
    public EntidadeDominio salvar(EntidadeDominio entidade) throws Exception {
        Cidade cidade = (Cidade) entidade;
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO cidade(cid_cidade, cid_uf_id, cid_dt_cadastro) ");
        sql.append("VALUES (?,?,?)");

        try {
            if (connection == null) {
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            IDAO ufDAO = new UfDAO(connection);
            cidade.setUf(salvaUf(cidade, ufDAO));
            logger.log(Level.INFO, "uf salva: " + cidade.getUf().getUf());
            cidade.complementarDtCadastro();
            logger.log(Level.INFO, "salvando cidade: " + cidade.getCidade());
            try (PreparedStatement pst = connection.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, cidade.getCidade());
                pst.setInt(2, cidade.getUf().getId());
                pst.setTimestamp(3, new Timestamp(cidade.getDtCadastro().getTime()));

                pst.executeUpdate();

                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idCidade = rs.getInt(1);
                        cidade.setId(idCidade);
                    }
                }
                return cidade;
            }
        }catch(Exception e){
            try{
                connection.rollback();
            }catch(SQLException rollbackEx){
                logger.log(Level.SEVERE, "Erro ao tentar realizar o rollback " + rollbackEx.getMessage(), rollbackEx);
            }
            logger.log(Level.SEVERE, "Erro ao salvar cidade: " + e.getMessage() + " " + cidade, e);
            throw new Exception("Erro ao salvar a cidade: " + e.getMessage() + " " + cidade, e); // Lançar exceção em vez de retornar null
        }
    }

    private Uf salvaUf(Cidade cidade, IDAO ufDAO) throws Exception {
        try{
            Uf uf = (Uf) ufDAO.salvar(cidade.getUf());
            if(uf!= null){
                return uf;
            } else {
                throw new Exception("Falha ao salvar a UF: a UF retornada é nula.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar UF: " + e.getMessage()+ e);
            throw new Exception("Erro ao salvar a UF: " + e.getMessage(), e);
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
