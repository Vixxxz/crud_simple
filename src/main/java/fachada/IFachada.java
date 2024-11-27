package fachada;

import dominio.EntidadeDominio;

import java.util.List;

public interface IFachada {
    List<EntidadeDominio> salvar(List<EntidadeDominio> entidades, StringBuilder sb) throws Exception;
    void alterar(EntidadeDominio entidade, StringBuilder sb) throws Exception;
    void excluir(EntidadeDominio entidade, StringBuilder sb);
    List<EntidadeDominio> consultar(EntidadeDominio entidade) throws Exception;
}
