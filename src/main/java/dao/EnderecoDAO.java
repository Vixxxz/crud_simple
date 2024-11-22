package dao;

import com.mysql.cj.log.Log;
import dominio.*;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnderecoDAO implements IDAO{
    private Connection connection;
    private static final Logger logger = Logger.getLogger(EnderecoDAO.class.getName());

    public EnderecoDAO(Connection connection){
        this.connection = connection;
    }

    @Override
    public EntidadeDominio salvar(EntidadeDominio entidade) throws Exception {
        Endereco endereco = (Endereco) entidade;
        StringBuilder sql = new StringBuilder();

        sql.append("INSERT INTO endereco(end_cep, end_bai_id, end_lgr_id, end_dt_cadastro) ");
        sql.append("VALUES(?,?,?,?)");

        try{
            if(connection == null){
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            IDAO logradouroDAO = new LogradouroDAO(connection);
            IDAO bairroDAO = new BairroDAO(connection);

            List<EntidadeDominio> entidades = logradouroDAO.consultar(endereco.getLogradouro());
            if(entidades.isEmpty()){
                endereco.setLogradouro(salvaLogradouro(endereco, logradouroDAO));
                logger.log(Level.INFO, "logradouro salvo: " + endereco.getLogradouro().getLogradouro());
            }
            else{
                endereco.setLogradouro((Logradouro) entidades.getFirst());
            }

            entidades = bairroDAO.consultar(endereco.getBairro());
            if(entidades.isEmpty()){
                endereco.setBairro(salvaBairro(endereco, bairroDAO));
                logger.log(Level.INFO, "bairro salvo: " + endereco.getBairro().getBairro());
            }
            else{
                endereco.setBairro((Bairro) entidades.getFirst());
            }

            endereco.complementarDtCadastro();

            logger.log(Level.INFO, "salvando endereco: " + endereco.getCep());
            try(PreparedStatement pst = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)){
                pst.setString(1, endereco.getCep());
                pst.setInt(2, endereco.getBairro().getId());
                pst.setInt(3, endereco.getLogradouro().getId());
                pst.setTimestamp(4, new Timestamp(endereco.getDtCadastro().getTime()));

                pst.executeUpdate();

                try(ResultSet rs = pst.getGeneratedKeys()){
                    if(rs.next()){
                        int idEndereco = rs.getInt(1);
                        endereco.setId(idEndereco);
                    }
                }
                return endereco;
            }
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar endereco: " + e.getMessage() + " " + endereco, e);
            throw new Exception("Erro ao salvar o endereco: " + e.getMessage() + " " + endereco, e); // Lançar exceção em vez de retornar null
        }
    }

    private Bairro salvaBairro(Endereco endereco, IDAO bairroDAO) throws Exception {
        try {
            Bairro bairro = (Bairro) bairroDAO.salvar(endereco.getBairro());
            if (bairro != null) {
                return bairro;
            } else {
                throw new Exception("Falha ao salvar o bairro: o bairro retornado é nulo.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar bairro: " + e.getMessage(),e);
            throw new Exception("Erro ao salvar o bairro: " + e.getMessage(),e);
        }
    }

    private Logradouro salvaLogradouro(Endereco endereco, IDAO logradouroDAO) throws Exception {
        try {
            Logradouro logradouro = (Logradouro) logradouroDAO.salvar(endereco.getLogradouro());
            if (logradouro != null) {
                return logradouro;
            } else {
                throw new Exception("Falha ao salvar o logradouro: o logradouro retornado é nulo.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar logradouro: " + e.getMessage(),e);
            throw new Exception("Erro ao salvar o logradouro: " + e.getMessage(), e);
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
        Endereco endereco = (Endereco) entidade;
        try {
            List<EntidadeDominio> enderecos = new ArrayList<>();
            List<Object> parametros = new ArrayList<>();
            StringBuilder sql = construirConsultaEndereco(endereco, parametros);

            try (PreparedStatement pst = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < parametros.size(); i++) {
                    pst.setObject(i + 1, parametros.get(i));
                }
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        enderecos.add(mapeiaEndereco(rs));
                    }
                }
            }
            return enderecos;
        } catch (Exception e) {
            throw new Exception("Erro ao consultar ClienteEndereco", e);
        }
    }

    private StringBuilder construirConsultaEndereco(Endereco endereco, List<Object> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM crud_v2.endereco e ")
                .append("INNER JOIN crud_v2.bairro b            ON e.end_bai_id = b.bai_id ")
                .append("INNER JOIN crud_v2.logradouro l        ON e.end_lgr_id = l.lgr_id ")
                .append("INNER JOIN crud_v2.tipo_logradouro tl  ON l.lgr_tpl_id = tl.tpl_id ")
                .append("INNER JOIN crud_v2.cidade c            ON b.bai_cid_id = c.cid_id ")
                .append("INNER JOIN crud_v2.uf u                ON c.cid_uf_id = u.uf_id ")
                .append("INNER JOIN crud_v2.pais p              ON u.pai_pai_id = p.pai_id ")
                .append("WHERE 1=1 ");

        adicionarCondicao(sql, "e.end_id = ?", endereco.getId(), parametros);
        adicionarCondicao(sql, "e.end_cep = ?", endereco.getCep(), parametros, true);

        if (endereco.getBairro() != null) {
            Bairro bairro = endereco.getBairro();
            adicionarCondicao(sql, "b.bai_id = ?", bairro.getId(), parametros);
            adicionarCondicao(sql, "b.bai_bairro = ?", bairro.getBairro(), parametros, true);

            if (bairro.getCidade() != null) {
                Cidade cidade = bairro.getCidade();
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
            }
        }

        if (endereco.getLogradouro() != null) {
            Logradouro logradouro = endereco.getLogradouro();
            adicionarCondicao(sql, "l.lgr_id = ?", logradouro.getId(), parametros);
            adicionarCondicao(sql, "l.lgr_logradouro = ?", logradouro.getLogradouro(), parametros, true);

            if (logradouro.getTpLogradouro() != null) {
                TipoLogradouro tpLogradouro = logradouro.getTpLogradouro();
                adicionarCondicao(sql, "tl.tpl_id = ?", tpLogradouro.getId(), parametros);
                adicionarCondicao(sql, "tl.tpl_tipo = ?", tpLogradouro.getTpLogradouro(), parametros, true);
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

    private Endereco mapeiaEndereco(ResultSet rs) throws SQLException {
        Endereco e = new Endereco();
        e.setId(rs.getInt("end_id"));
        e.setCep(rs.getString("end_cep"));

        Bairro b = new Bairro();
        b.setId(rs.getInt("bai_id"));
        b.setBairro(rs.getString("bai_bairro"));

        Cidade c = new Cidade();
        c.setId(rs.getInt("cid_id"));
        c.setCidade(rs.getString("cid_cidade"));

        Uf u = new Uf();
        u.setId(rs.getInt("uf_id"));
        u.setUf(rs.getString("uf_uf"));

        Pais p = new Pais();
        p.setId(rs.getInt("pai_id"));
        p.setPais(rs.getString("pai_pais"));

        Logradouro l = new Logradouro();
        l.setId(rs.getInt("lgr_id"));
        l.setLogradouro(rs.getString("lgr_logradouro"));

        TipoLogradouro tl = new TipoLogradouro();
        tl.setId(rs.getInt("tpl_id"));
        tl.setTpLogradouro(rs.getString("tpl_tipo"));

        u.setPais(p);
        c.setUf(u);
        b.setCidade(c);
        e.setBairro(b);

        l.setTpLogradouro(tl);
        e.setLogradouro(l);

        return e;
    }
}
