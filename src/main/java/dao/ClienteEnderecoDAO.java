package dao;

import dominio.*;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteEnderecoDAO implements IDAO{
    private Connection connection;
    private static final Logger logger = Logger.getLogger(ClienteEnderecoDAO.class.getName());

    public ClienteEnderecoDAO(Connection connection){
        this.connection = connection;
    }

    @Override
    public EntidadeDominio salvar(EntidadeDominio entidade) throws Exception {
        ClienteEndereco clienteEndereco = (ClienteEndereco) entidade;
        StringBuilder sql = new StringBuilder();

        sql.append("INSERT INTO cliente_endereco(cli_end_cli_id, cli_end_end_id, cli_end_num, ");
        sql.append("cli_end_tp_residencia, cli_end_tp_end, cli_end_obs, cli_end_dt_cadastro) ");
        sql.append("VALUES (?,?,?,?,?,?,?)");

        try {
            if (connection == null) {
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            IDAO enderecoDAO = new EnderecoDAO(connection);
            List<EntidadeDominio> enderecos = enderecoDAO.consultar(clienteEndereco.getEndereco());
            if(enderecos.isEmpty()) {
                clienteEndereco.setEndereco(salvaEndereco(clienteEndereco, enderecoDAO));
                logger.log(Level.INFO, "endereco salvo: " + clienteEndereco.getEndereco().getCep());
            }
            else{
                clienteEndereco.setEndereco((Endereco) enderecos.getFirst());
            }
            clienteEndereco.complementarDtCadastro();

            logger.log(Level.INFO, "salvando cliente endereco: " + clienteEndereco.getNumero());
            try (PreparedStatement pst = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
                pst.setInt(1, clienteEndereco.getCliente().getId());
                pst.setInt(2, clienteEndereco.getEndereco().getId());
                pst.setString(3, clienteEndereco.getNumero());
                pst.setString(4, clienteEndereco.getTipoResidencia());
                pst.setString(5, clienteEndereco.getTipoEndereco());
                pst.setString(6, clienteEndereco.getObservacoes());
                pst.setTimestamp(7, new Timestamp(clienteEndereco.getDtCadastro().getTime()));

                pst.executeUpdate();

                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idClienteEndereco = rs.getInt(1);
                        clienteEndereco.setId(idClienteEndereco);
                    }
                }
                return clienteEndereco;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao salvar ClienteEndereco: " + e.getMessage() + " " + clienteEndereco, e);
            throw new Exception("Erro ao salvar ClienteEndereco: " + e.getMessage() + " " + clienteEndereco, e);
        }
    }

    private Endereco salvaEndereco(ClienteEndereco clienteEndereco, IDAO enderecoDAO) throws Exception {
        try {
            Endereco endereco = (Endereco) enderecoDAO.salvar(clienteEndereco.getEndereco());
            if (endereco != null) {
                return endereco;
            } else {
                throw new Exception("Falha ao salvar o endereço: o endereço retornado é nulo.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar endereco: " + e.getMessage(), e);
            throw new Exception("Erro ao salvar o endereço: " + e.getMessage(), e);
        }
    }

    @Override
    public EntidadeDominio alterar(EntidadeDominio entidade) throws Exception {
        ClienteEndereco clienteEndereco = (ClienteEndereco) entidade;

        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE cliente_endereco SET ");
        sql.append("cli_end_num = ?, ");
        sql.append("cli_end_tp_residencia = ?, ");
        sql.append("cli_end_tp_end = ?, ");
        sql.append("cli_end_obs = ?, ");
        sql.append("cli_end_dt_cadastro = ?, "); // Correção: adição da vírgula aqui
        sql.append("cli_end_cli_id = ?, ");
        sql.append("cli_end_end_id = ? ");
        sql.append("WHERE cli_end_id = ?");

        try {
            if (connection == null || connection.isClosed()) {
                connection = Conexao.getConnectionMySQL();
            }
            connection.setAutoCommit(false);

            ClienteDAO clienteDAO = new ClienteDAO(connection);
            List<EntidadeDominio> entidades = clienteDAO.consultar(clienteEndereco.getCliente());

            if (entidades.isEmpty()) {
                throw new Exception("Cliente não encontrado");
            } else {
                clienteEndereco.setCliente((Cliente) entidades.get(0)); // Corrigido para usar `get(0)`
            }

            if (clienteEndereco.getEndereco() != null) {
                IDAO enderecoDAO = new EnderecoDAO(connection);
                entidades = enderecoDAO.consultar(clienteEndereco.getEndereco());
                if (entidades.isEmpty()) {
                    clienteEndereco.setEndereco(atualizaEndereco(clienteEndereco, enderecoDAO));
                    logger.log(Level.INFO, "Endereço atualizado para ClienteEndereco: " + clienteEndereco.getEndereco().getCep());
                } else {
                    clienteEndereco.setEndereco((Endereco) entidades.get(0));
                }
            }

            clienteEndereco.complementarDtCadastro();
            try (PreparedStatement pst = connection.prepareStatement(sql.toString())) {
                pst.setString(1, clienteEndereco.getNumero());
                pst.setString(2, clienteEndereco.getTipoResidencia());
                pst.setString(3, clienteEndereco.getTipoEndereco());
                pst.setString(4, clienteEndereco.getObservacoes());
                pst.setTimestamp(5, new Timestamp(clienteEndereco.getDtCadastro().getTime()));
                pst.setInt(6, clienteEndereco.getCliente().getId());
                pst.setInt(7, clienteEndereco.getEndereco().getId());
                pst.setInt(8, clienteEndereco.getId());

                int rowsUpdated = pst.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new Exception("Nenhum registro encontrado para o ClienteEndereco com ID: " + clienteEndereco.getId());
                }
            }

            connection.commit();
            return clienteEndereco;
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar ClienteEndereco: " + e.getMessage(), e);
        }
    }

    private Endereco atualizaEndereco(ClienteEndereco clienteEndereco, IDAO enderecoDAO) throws Exception {
        try {
            Endereco endereco = (Endereco) enderecoDAO.alterar(clienteEndereco.getEndereco());
            if (endereco != null) {
                return endereco;
            } else {
                throw new Exception("Falha ao alterar o endereço: o endereço retornado é nulo.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao alterar endereco: " + e.getMessage(), e);
            throw new Exception("Erro ao alterar o endereço: " + e.getMessage(), e);
        }
    }


    @Override
    public void excluir(EntidadeDominio entidade) {

    }

    @Override
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) throws Exception {
        ClienteEndereco clienteEndereco = (ClienteEndereco) entidade;
        List<EntidadeDominio> clientesEnderecos = new ArrayList<>();
        List<Object> parametros = new ArrayList<>();

        try {
            StringBuilder sql = construirConsulta(clienteEndereco, parametros);

            try (PreparedStatement pst = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < parametros.size(); i++) {
                    pst.setObject(i + 1, parametros.get(i));
                }

                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        clientesEnderecos.add(mapeiaClienteEndereco(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Erro ao consultar ClienteEndereco: " + e.getMessage(), e);
        }
        return clientesEnderecos;
    }

    private StringBuilder construirConsulta(ClienteEndereco clienteEndereco, List<Object> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ")
                .append("FROM crud_v2.cliente_endereco ce ")
                .append("INNER JOIN crud_v2.cliente c           ON c.cli_id = ce.cli_end_cli_id ")
                .append("INNER JOIN crud_v2.endereco e          ON e.end_id = ce.cli_end_end_id ")
                .append("INNER JOIN crud_v2.bairro b            ON e.end_bai_id = b.bai_id ")
                .append("INNER JOIN crud_v2.logradouro l        ON e.end_lgr_id = l.lgr_id ")
                .append("INNER JOIN crud_v2.tipo_logradouro tl  ON l.lgr_tpl_id = tl.tpl_id ")
                .append("INNER JOIN crud_v2.cidade cd           ON b.bai_cid_id = cd.cid_id ")
                .append("INNER JOIN crud_v2.uf u                ON cd.cid_uf_id = u.uf_id ")
                .append("INNER JOIN crud_v2.pais p              ON u.pai_pai_id = p.pai_id ")
                .append("WHERE 1=1 ");

        adicionarCondicoesClienteEndereco(sql, clienteEndereco, parametros);

        return sql;
    }

    private void adicionarCondicoesClienteEndereco(StringBuilder sql, ClienteEndereco clienteEndereco, List<Object> parametros) {
        adicionarCondicao(sql, "ce.cli_end_id = ?", clienteEndereco.getId(), parametros);
        adicionarCondicao(sql, "ce.cli_end_num = ?", clienteEndereco.getNumero(), parametros, true);
        adicionarCondicao(sql, "ce.cli_end_tp_end = ?", clienteEndereco.getTipoEndereco(), parametros, true);
        adicionarCondicao(sql, "ce.cli_end_tp_residencia = ?", clienteEndereco.getTipoResidencia(), parametros, true);
        adicionarCondicao(sql, "ce.cli_end_obs = ?", clienteEndereco.getObservacoes(), parametros, true);

        if (clienteEndereco.getCliente() != null) {
            adicionarCondicoesCliente(sql, clienteEndereco.getCliente(), parametros);
        }

        if (clienteEndereco.getEndereco() != null) {
            adicionarCondicoesEndereco(sql, clienteEndereco.getEndereco(), parametros);
        }
    }

    private void adicionarCondicoesCliente(StringBuilder sql, Cliente cliente, List<Object> parametros) {
        adicionarCondicao(sql, "c.cli_id = ?", cliente.getId(), parametros);
        adicionarCondicao(sql, "c.cli_ranking = ?", cliente.getRanking(), parametros, true);
        adicionarCondicao(sql, "c.cli_nome = ?", cliente.getNome(), parametros, true);
        adicionarCondicao(sql, "c.cli_genero = ?", cliente.getGenero(), parametros, true);
        adicionarCondicao(sql, "c.cli_cpf = ?", cliente.getCpf(), parametros, true);
        adicionarCondicao(sql, "c.cli_tp_tel = ?", cliente.getTipoTelefone(), parametros, true);
        adicionarCondicao(sql, "c.cli_tel = ?", cliente.getTelefone(), parametros, true);
        adicionarCondicao(sql, "c.cli_email = ?", cliente.getEmail(), parametros, true);
        adicionarCondicao(sql, "c.cli_senha = ?", cliente.getSenha(), parametros, true);
        adicionarCondicao(sql, "c.cli_dt_nasc = ?", cliente.getDataNascimento(), parametros);
    }

    private void adicionarCondicoesEndereco(StringBuilder sql, Endereco endereco, List<Object> parametros) {
        adicionarCondicao(sql, "e.end_id = ?", endereco.getId(), parametros);
        adicionarCondicao(sql, "e.end_cep = ?", endereco.getCep(), parametros, true);
        if (endereco.getBairro() != null) {
            adicionarCondicaoBairro(sql, endereco.getBairro(), parametros);
        }
        if (endereco.getLogradouro() != null) {
            adicionarCondicaoLogradouro(sql, endereco.getLogradouro(), parametros);
        }
    }

    private void adicionarCondicaoLogradouro(StringBuilder sql, Logradouro logradouro, List<Object> parametros) {
        adicionarCondicao(sql, "l.lgr_id = ?", logradouro.getId(), parametros);
        adicionarCondicao(sql, "l.lgr_logradouro = ?", logradouro.getLogradouro(), parametros, true);

        if(logradouro.getTpLogradouro() != null){
            adicionarCondicaoTpl(sql, logradouro.getTpLogradouro(), parametros);
        }
    }

    private void adicionarCondicaoTpl(StringBuilder sql, TipoLogradouro tpLogradouro, List<Object> parametros) {
        adicionarCondicao(sql, "tl.tpl_id = ?", tpLogradouro.getId(), parametros);
        adicionarCondicao(sql, "tl.tpl_tipo = ?", tpLogradouro.getTpLogradouro(), parametros, true);
    }

    private void adicionarCondicaoBairro(StringBuilder sql, Bairro bairro, List<Object> parametros) {
        adicionarCondicao(sql, "b.bai_id = ?", bairro.getId(), parametros);
        adicionarCondicao(sql, "b.bai_bairro = ?", bairro.getBairro(), parametros, true);

        if (bairro.getCidade() != null) {
            adicionarCondicaoCidade(sql, bairro.getCidade(), parametros);
        }
    }

    private void adicionarCondicaoCidade(StringBuilder sql, Cidade cidade, List<Object> parametros) {
        adicionarCondicao(sql, "cd.cid_id = ?", cidade.getId(), parametros);
        adicionarCondicao(sql, "cd.cid_cidade = ?", cidade.getCidade(), parametros, true);

        if (cidade.getUf() != null) {
            adicionarCondicaoUf(sql, cidade.getUf(), parametros);
        }
    }

    private void adicionarCondicaoUf(StringBuilder sql, Uf uf, List<Object> parametros) {
        adicionarCondicao(sql, "u.uf_id = ?", uf.getId(), parametros);
        adicionarCondicao(sql, "u.uf_uf = ?", uf.getUf(), parametros, true);

        if (uf.getPais() != null) {
            adicionarCondicaoPais(sql, uf.getPais(), parametros);
        }
    }

    private void adicionarCondicaoPais(StringBuilder sql, Pais pais, List<Object> parametros) {
        adicionarCondicao(sql, "p.pai_id = ?", pais.getId(), parametros);
        adicionarCondicao(sql, "p.pai_pais = ?", pais.getPais(), parametros, true);
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

    private ClienteEndereco mapeiaClienteEndereco(ResultSet rs) throws SQLException {
        ClienteEndereco ce = new ClienteEndereco();
        ce.setId(rs.getInt("cli_end_id"));
        ce.setNumero(rs.getString("cli_end_num"));
        ce.setTipoResidencia(rs.getString("cli_end_tp_residencia"));
        ce.setTipoEndereco(rs.getString("cli_end_tp_end"));
        ce.setObservacoes(rs.getString("cli_end_obs"));

        Endereco end = new Endereco();
        end.setId(rs.getInt("end_id"));
        end.setCep(rs.getString("end_cep"));

        Cliente cli = new Cliente();
        cli.setId(rs.getInt("cli_id"));
        cli.setRanking(rs.getString("cli_ranking"));
        cli.setNome(rs.getString("cli_nome"));
        cli.setGenero(rs.getString("cli_genero"));
        cli.setCpf(rs.getString("cli_cpf"));
        cli.setTipoTelefone(rs.getString("cli_tp_tel"));
        cli.setTelefone(rs.getString("cli_tel"));
        cli.setEmail(rs.getString("cli_email"));
        cli.setSenha(rs.getString("cli_senha"));
        cli.setDataNascimento(rs.getDate("cli_dt_nasc"));

        Bairro bai = new Bairro();
        bai.setId(rs.getInt("bai_id"));
        bai.setBairro(rs.getString("bai_bairro"));

        Logradouro lgr = new Logradouro();
        lgr.setId(rs.getInt("lgr_id"));
        lgr.setLogradouro(rs.getString("lgr_logradouro"));

        TipoLogradouro tpLogradouro = new TipoLogradouro();
        tpLogradouro.setId(rs.getInt("tpl_id"));
        tpLogradouro.setTpLogradouro(rs.getString("tpl_tipo"));

        Cidade cidade = new Cidade();
        cidade.setId(rs.getInt("cid_id"));
        cidade.setCidade(rs.getString("cid_cidade"));

        Uf uf = new Uf();
        uf.setId(rs.getInt("uf_id"));
        uf.setUf(rs.getString("uf_uf"));

        Pais pais = new Pais();
        pais.setId(rs.getInt("pai_id"));
        pais.setPais(rs.getString("pai_pais"));

        uf.setPais(pais);
        cidade.setUf(uf);
        bai.setCidade(cidade);
        lgr.setTpLogradouro(tpLogradouro);
        end.setLogradouro(lgr);
        end.setBairro(bai);

        ce.setCliente(cli);
        ce.setEndereco(end);

        return ce;
    }
}