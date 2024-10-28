package controle;

import dao.ClienteDAO;
import dao.IDAO;
import dominio.Cliente;
import dominio.EntidadeDominio;
import strategy.*;

import java.util.ArrayList;
import java.util.List;

public class Fachada implements IFachada{
    @Override
    public void salvar(EntidadeDominio entidade, StringBuilder sb) throws Exception {
        Cliente cliente = (Cliente)entidade;
        List<IStrategy> validacoes = new ArrayList<>();
        validacoes.add(new ValidaDados());
        validacoes.add(new ValidaCpf());
        validacoes.add(new ValidaEmail());
        validacoes.add(new ValidaSenha());
        validacoes.add(new ValidaEndereco());
        validacoes.add(new ValidaTelefone());

        for(IStrategy strategy : validacoes){
            strategy.processar(cliente, sb);
        }

        if(sb.isEmpty()){
            try{
                cliente.complementarDtCadastro();
                IDAO clienteDAO = new ClienteDAO();
                clienteDAO.salvar(cliente);
            } catch (Exception e) {
                throw new Exception("Erro ao salvar o cliente: " + e.getMessage() + " " + cliente, e);
            }
        }else{
            throw new Exception("Existem erros de validação: " + sb.toString());
        }
    }

    @Override
    public void alterar(EntidadeDominio entidade, StringBuilder sb) {
    }

    @Override
    public void excluir(EntidadeDominio entidade, StringBuilder sb) {
    }

    @Override
    public List<EntidadeDominio> consultar(EntidadeDominio entidade, StringBuilder sb) {
        return List.of();
    }
}
