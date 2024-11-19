package strategy;

import dominio.EntidadeDominio;

public interface IStrategy {
    public String processar(EntidadeDominio entidade, StringBuilder sb);
}