package dao;

import dominio.EntidadeDominio;

import java.sql.SQLException;
import java.util.List;

public interface IDAO {
    EntidadeDominio salvar(EntidadeDominio entidade) throws Exception;
    void alterar(EntidadeDominio entidade) throws Exception;
    void excluir(EntidadeDominio entidade);
    List<EntidadeDominio> consultar(EntidadeDominio entidade) throws Exception;
}
