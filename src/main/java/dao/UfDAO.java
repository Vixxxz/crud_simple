package dao;

import dominio.EntidadeDominio;
import dominio.Pais;
import dominio.Uf;
import util.Conexao;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UfDAO implements IDAO{
    private Connection connection;
    private static final Logger logger = Logger.getLogger(UfDAO.class.getName());

    public UfDAO(Connection connection){
        this.connection = connection;
    }

    @Override
    public EntidadeDominio salvar(EntidadeDominio entidade) throws Exception {
        Uf uf = (Uf) entidade;
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO uf(uf_uf, pai_pai_id, uf_dt_cadastro) ");
        sql.append(" VALUES (?,?,?)");

        try{
            if(connection == null){
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            IDAO paisDAO = new PaisDAO(connection);
            Pais pais = salvaPais(uf, paisDAO);
            uf.setPais(pais);
            logger.log(Level.INFO, "pais salvo: " + pais);
            uf.complementarDtCadastro();

            try(PreparedStatement pst = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)){
                pst.setString(1, uf.getUf());
                pst.setInt(2, uf.getPais().getId());
                pst.setTimestamp(3, new Timestamp(uf.getDtCadastro().getTime()));

                pst.executeUpdate();

                try(ResultSet rs = pst.getGeneratedKeys()){
                    if(rs.next()){
                        int idUf = rs.getInt(1);
                        uf.setId(idUf);
                    }
                }
                return uf;
            }
        }catch(Exception e){
            logger.log(Level.SEVERE, "Erro ao salvar UF: " + e.getMessage() + " " + uf, e);
            throw new Exception("Erro ao salvar a uf: " + e.getMessage() + " " + uf, e);
        }
    }

    private Pais salvaPais(Uf uf, IDAO paisDAO) throws Exception {
        try{
            Pais pais = (Pais) paisDAO.salvar(uf.getPais());
            if(pais!= null){
                return pais;
            } else {
                throw new Exception("Falha ao salvar o pais: o pais retornado é nulo.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar o pais: " + e);
            throw new Exception("Erro ao salvar o pais: " + e.getMessage(), e);
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
