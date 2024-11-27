package dao;

import dominio.EntidadeDominio;
import dominio.Pais;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaisDAO implements IDAO{
    private Connection connection;
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
            }
            connection.setAutoCommit(false);

            pais.complementarDtCadastro();

            logger.log(Level.INFO, "salvando pais: " + pais.getPais());

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
                return pais;
            }
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar pais: " + e.getMessage() + " " + pais, e);
            throw new Exception("Erro ao salvar o pais: " + e.getMessage() + " " + pais, e); // Lançar exceção em vez de retornar null
        }
    }

    @Override
    public EntidadeDominio alterar(EntidadeDominio entidade) throws Exception {
        Pais pais = (Pais) entidade;
        StringBuilder sqlUpdatePais = new StringBuilder();
        sqlUpdatePais.append("UPDATE pais SET pai_pais = ?, pai_dt_cadastro = ? ")
                .append("WHERE pai_id = ?");

        try {
            if (connection == null || connection.isClosed()) {
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            pais.complementarDtCadastro(); // Atualiza a data de cadastro

            logger.log(Level.INFO, "Atualizando país com ID: " + pais.getId());
            try (PreparedStatement pst = connection.prepareStatement(sqlUpdatePais.toString())) {
                pst.setString(1, pais.getPais());
                pst.setTimestamp(2, new Timestamp(pais.getDtCadastro().getTime()));
                pst.setInt(3, pais.getId());

                int rowsUpdated = pst.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new Exception("Nenhum país encontrado para o ID " + pais.getId());
                }
            }

            connection.commit();
            return pais;

        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar pais: " + e.getMessage(), e);
        }
    }


    @Override
    public void excluir(EntidadeDominio entidade) {

    }

    @Override
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) throws Exception {
        Pais pais = (Pais) entidade;
        List<EntidadeDominio> paises = new ArrayList<>();
        List<Object> parametros = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("select * from crud_v2.pais p ");
        sql.append("where 1=1 ");

        if (pais.getId() != null) {
            sql.append(" and p.pai_id = ? ");
            parametros.add(pais.getId());
        }
        if (isStringValida(pais.getPais())) {
            sql.append(" and p.pai_pais = ? ");
            parametros.add(pais.getPais());
        }

        try (PreparedStatement pst = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                pst.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Pais p = new Pais();
                    p.setId(rs.getInt("pai_id"));
                    p.setPais(rs.getString("pai_pais"));
                    paises.add(p);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao consultar pais: " + e.getMessage(), e);
        }
        return paises;
    }

    private boolean isStringValida(String value) {
        return value != null && !value.isBlank();
    }
}
