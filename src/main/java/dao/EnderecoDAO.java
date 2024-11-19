package dao;

import dominio.Bairro;
import dominio.Endereco;
import dominio.EntidadeDominio;
import dominio.Logradouro;
import util.Conexao;

import java.sql.*;
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

            endereco.setLogradouro(salvaLogradouro(endereco, logradouroDAO));
            logger.log(Level.INFO, "logradouro salvo: " + endereco.getLogradouro().getLogradouro());
            endereco.setBairro(salvaBairro(endereco, bairroDAO));
            logger.log(Level.INFO, "bairro salvo: " + endereco.getBairro().getBairro());
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
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) {
        return List.of();
    }
}
