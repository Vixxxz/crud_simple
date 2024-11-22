package dao;

import dominio.*;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BairroDAO implements IDAO{
    private Connection connection;
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
            }
            connection.setAutoCommit(false);

            IDAO cidadeDAO = new CidadeDAO(connection);
            List<EntidadeDominio> cidades = cidadeDAO.consultar(bairro.getCidade());

            if(cidades.isEmpty()) {
                bairro.setCidade(salvaCidade(bairro, cidadeDAO));
                logger.log(Level.INFO, "cidade salva: " + bairro.getCidade().getCidade());
            }
            else{
                bairro.setCidade((Cidade) cidades.getFirst());
            }

            bairro.complementarDtCadastro();

            logger.log(Level.INFO, "salvando bairro: " + bairro.getBairro());
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
                return bairro;
            }
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar bairro: " + e.getMessage() + " " + bairro, e);
            throw new Exception("Erro ao salvar o bairro: " + e.getMessage() + " " + bairro, e); // Lançar exceção em vez de retornar null
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
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) throws Exception {
        Bairro bairro = (Bairro) entidade;
        try {
            List<EntidadeDominio> bairros = new ArrayList<>();
            List<Object> parametros = new ArrayList<>();

            // Construir a consulta SQL com base nos parâmetros
            StringBuilder sql = construirConsultaBairro(bairro, parametros);

            // Executar a consulta
            try (PreparedStatement pst = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < parametros.size(); i++) {
                    pst.setObject(i + 1, parametros.get(i));
                }

                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        bairros.add(mapeiaBairro(rs));
                    }
                }
            }
            return bairros;
        } catch (Exception e) {
            throw new Exception("Erro ao consultar bairro: " + e.getMessage(), e);
        }
    }

    private EntidadeDominio mapeiaBairro(ResultSet rs) throws SQLException {
        Bairro bairro = new Bairro();
        bairro.setId(rs.getInt("bai_id"));
        bairro.setBairro(rs.getString("bai_bairro"));

        // Mapeamento da Cidade
        Cidade cidade = new Cidade();
        cidade.setId(rs.getInt("cid_id"));
        cidade.setCidade(rs.getString("cid_cidade"));

        // Mapeamento de UF
        Uf uf = new Uf();
        uf.setId(rs.getInt("uf_id"));
        uf.setUf(rs.getString("uf_uf"));

        // Mapeamento de País
        Pais pais = new Pais();
        pais.setId(rs.getInt("pai_id"));
        pais.setPais(rs.getString("pai_pais"));

        // Relacionamento dos objetos
        uf.setPais(pais);
        cidade.setUf(uf);
        bairro.setCidade(cidade);

        return bairro;
    }

    private StringBuilder construirConsultaBairro(Bairro bairro, List<Object> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM crud_v2.bairro b ")
                .append("INNER JOIN crud_v2.cidade c ON b.bai_cid_id = c.cid_id ")
                .append("INNER JOIN crud_v2.uf u ON c.cid_uf_id = u.uf_id ")
                .append("INNER JOIN crud_v2.pais p ON u.pai_pai_id = p.pai_id ")
                .append("WHERE 1=1 ");

        // Adicionar filtros dinamicamente
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
