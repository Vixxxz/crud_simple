package dao;

import dominio.EntidadeDominio;
import dominio.Pais;
import dominio.Uf;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
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
            List<EntidadeDominio> paises = paisDAO.consultar(uf.getPais());

            if(paises.isEmpty()) {
                uf.setPais(salvaPais(uf, paisDAO));
                logger.log(Level.INFO, "pais salvo: " + uf.getPais().getPais());
            }
            else{
                uf.setPais((Pais) paises.getFirst());
            }

            uf.complementarDtCadastro();

            logger.log(Level.INFO, "salvando uf: " + uf.getUf());
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
    public EntidadeDominio alterar(EntidadeDominio entidade) throws Exception {
        Uf uf = (Uf) entidade;
        StringBuilder sqlUpdateUf = new StringBuilder();
        sqlUpdateUf.append("UPDATE uf SET uf_uf = ?, pai_pai_id = ?, uf_dt_cadastro = ? ")
                .append("WHERE uf_id = ?");

        try {
            if (connection == null || connection.isClosed()) {
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            List<EntidadeDominio> entidades;

            if (uf.getPais() != null) {
                IDAO paisDAO = new PaisDAO(connection);
                entidades = paisDAO.consultar(uf.getPais());
                if (entidades.isEmpty()) {
                    uf.setPais(atualizaPais(uf, paisDAO));
                    logger.log(Level.INFO, "pais atualizado");
                } else {
                    uf.setPais((Pais) entidades.getFirst());
                }
            }

            uf.complementarDtCadastro(); // Atualiza a data de cadastro
            logger.log(Level.INFO, "Atualizando UF com ID: " + uf.getId());
            try (PreparedStatement pst = connection.prepareStatement(sqlUpdateUf.toString())) {
                pst.setString(1, uf.getUf());
                pst.setInt(2, uf.getPais().getId());
                pst.setTimestamp(3, new Timestamp(uf.getDtCadastro().getTime()));
                pst.setInt(4, uf.getId());

                int rowsUpdated = pst.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new Exception("Nenhuma UF encontrada para o ID " + uf.getId());
                }
            }

            connection.commit();
            return uf;

        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar uf: " + e.getMessage(), e);
        }
    }

    private Pais atualizaPais(Uf uf, IDAO paisDAO) throws Exception {
        try{
            Pais pais = (Pais) paisDAO.alterar(uf.getPais());
            if(pais!= null){
                return pais;
            } else {
                throw new Exception("Falha ao alterar o pais: o pais retornado é nulo.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao alterar o pais: " + e);
            throw new Exception("Erro ao alterar o pais: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(EntidadeDominio entidade) {

    }

    @Override
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) throws Exception {
        Uf uf = (Uf) entidade;
        try{
            List<EntidadeDominio> ufs = new ArrayList<>();
            List<Object> parametros = new ArrayList<>();

            StringBuilder sql = new StringBuilder();
            sql.append("select * from crud_v2.uf u ");
            sql.append("inner join crud_v2.pais p on u.pai_pai_id = p.pai_id ");
            sql.append("where 1=1 ");

            if(uf.getId() != null){
                sql.append(" and u.uf_id = ? ");
                parametros.add(uf.getId());
            }
            if(isStringValida(uf.getUf())){
                sql.append(" and u.uf_uf = ? ");
                parametros.add(uf.getUf());
            }
            if(uf.getPais() != null){
                Pais p = uf.getPais();
                if(p.getId() != null){
                    sql.append(" and p.pai_id = ? ");
                    parametros.add(p.getId());
                }
                if(isStringValida(p.getPais())){
                    sql.append(" and p.pai_pais = ? ");
                    parametros.add(p.getPais());
                }
            }
            try(PreparedStatement pst = connection.prepareStatement(sql.toString())){
                for(int i = 0; i < parametros.size(); i++){
                    pst.setObject(i + 1, parametros.get(i));
                }
                try(ResultSet rs = pst.executeQuery()){
                    while (rs.next()){
                        Uf u = new Uf();
                        u.setId(rs.getInt("uf_id"));
                        u.setUf(rs.getString("uf_uf"));

                        Pais p = new Pais();
                        p.setId(rs.getInt("pai_id"));
                        p.setPais(rs.getString("pai_pais"));

                        u.setPais(p);

                        ufs.add(u);
                    }
                }
            }
            return ufs;
        } catch (Exception e) {
            throw new Exception("Erro ao buscar uf: " + e.getMessage(), e);
        }
    }

    private boolean isStringValida(String value) {
        return value != null && !value.isBlank();
    }
}
