package dao;

import dominio.Cidade;
import dominio.EntidadeDominio;
import dominio.Pais;
import dominio.Uf;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
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
            List<EntidadeDominio> ufs = ufDAO.consultar(cidade.getUf());

            if(ufs.isEmpty()){
                cidade.setUf(salvaUf(cidade, ufDAO));
                logger.log(Level.INFO, "uf salva: " + cidade.getUf().getUf());
            }
            else{
                cidade.setUf((Uf) ufs.getFirst());
            }

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
    public EntidadeDominio alterar(EntidadeDominio entidade) throws Exception {
        Cidade cidade = (Cidade) entidade;
        StringBuilder sqlUpdateCidade = new StringBuilder();
        sqlUpdateCidade.append("UPDATE cidade SET cid_cidade = ?, cid_uf_id = ?, cid_dt_cadastro = ? ")
                .append("WHERE cid_id = ?");

        try {
            if (connection == null || connection.isClosed()) {
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            List<EntidadeDominio> entidades;

            if (cidade.getUf() != null) {
                IDAO ufDAO = new UfDAO(connection);
                entidades = ufDAO.consultar(cidade.getUf());
                if (entidades.isEmpty()) {
                    cidade.setUf(atualizaUf(cidade, ufDAO));
                    logger.log(Level.INFO, "Uf atualizado");
                } else {
                    cidade.setUf((Uf) entidades.getFirst());
                }
            }

            cidade.complementarDtCadastro();

            logger.log(Level.INFO, "Atualizando cidade com ID: " + cidade.getId());
            try (PreparedStatement pst = connection.prepareStatement(sqlUpdateCidade.toString())) {
                pst.setString(1, cidade.getCidade());
                pst.setInt(2, cidade.getUf().getId());
                pst.setTimestamp(3, new Timestamp(cidade.getDtCadastro().getTime()));
                pst.setInt(4, cidade.getId());

                int rowsUpdated = pst.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new Exception("Nenhuma cidade encontrada para o ID " + cidade.getId());
                }
            }

            connection.commit();
            return cidade;

        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar cidade: " + e.getMessage(), e);
        }
    }

    private Uf atualizaUf(Cidade cidade, IDAO ufDAO) throws Exception {
        try{
            Uf uf = (Uf) ufDAO.alterar(cidade.getUf());
            if(uf!= null){
                return uf;
            } else {
                throw new Exception("Falha ao alterar a UF: a UF retornada é nula.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao alterar UF: " + e.getMessage()+ e);
            throw new Exception("Erro ao alterar a UF: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(EntidadeDominio entidade) {

    }

    @Override
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) throws Exception {
        Cidade cidade = (Cidade) entidade;
        try {
            List<EntidadeDominio> cidades = new ArrayList<>();
            List<Object> parametros = new ArrayList<>();

            StringBuilder sql = construirConsultaCidade(cidade, parametros);

            try (PreparedStatement pst = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < parametros.size(); i++) {
                    pst.setObject(i + 1, parametros.get(i));
                }
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        cidades.add(mapeiaCidade(rs));
                    }
                }
            }
            return cidades;
        } catch (Exception e) {
            throw new Exception("Erro ao consultar cidade: " + e.getMessage(), e);
        }
    }

    private EntidadeDominio mapeiaCidade(ResultSet rs) throws SQLException {
        Cidade c = new Cidade();
        c.setId(rs.getInt("cid_id"));
        c.setCidade(rs.getString("cid_cidade"));

        Uf u = new Uf();
        u.setId(rs.getInt("uf_id"));
        u.setUf(rs.getString("uf_uf"));

        Pais p = new Pais();
        p.setId(rs.getInt("pai_id"));
        p.setPais(rs.getString("pai_pais"));

        u.setPais(p);
        c.setUf(u);

        return c;
    }

    private StringBuilder construirConsultaCidade(Cidade cidade, List<Object> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM crud_v2.cidade c ")
                .append("INNER JOIN crud_v2.uf u ON c.cid_uf_id = u.uf_id ")
                .append("INNER JOIN crud_v2.pais p ON u.pai_pai_id = p.pai_id ")
                .append("WHERE 1=1 ");

        adicionarCondicao(sql, "c.cid_id = ?", cidade.getId(), parametros);
        adicionarCondicao(sql, "c.cid_cidade = ?", cidade.getCidade(), parametros, true);

        if (cidade.getUf() != null) {
            Uf uf = cidade.getUf();
            adicionarCondicao(sql, "u.uf_id = ?", uf.getId(), parametros);
            adicionarCondicao(sql, "u.uf_uf = ?", uf.getUf(), parametros, true);

            if (uf.getPais() != null) {
                Pais pais = uf.getPais();
                adicionarCondicao(sql, "p.pai_id = ?", pais.getId(), parametros);
                adicionarCondicao(sql, "p.pai_pais = ?", pais.getPais(), parametros, true);
            }
        }

        return sql;
    }

    private void adicionarCondicao(StringBuilder sql, String condicao, Object valor, List<Object> parametros) {
        adicionarCondicao(sql, condicao, valor, parametros, false);
    }

    private void adicionarCondicao(StringBuilder sql, String condicao, Object valor, List<Object> parametros, boolean isString) {
        if ((isString && isStringValida((String) valor)) || (!isString && valor != null)) {
            sql.append(" AND ").append(condicao).append(" ");
            parametros.add(valor);
        }
    }

    private boolean isStringValida(String value) {
        return value != null && !value.isBlank();
    }
}
