package strategy;

import java.security.MessageDigest; //calcula o valor hash da senha do algoritmo SHA-256
import java.security.NoSuchAlgorithmException;
import java.util.Base64; //codifica e decodifica dados no formato base64, transforma o hash em legível


import dominio.Cliente;
import dominio.EntidadeDominio;

public class EncriptografaSenha implements IStrategy{

    @Override
    public String processar(EntidadeDominio entidade, StringBuilder sb){
        Cliente cliente = (Cliente) entidade;
        String senha = cliente.getSenha();
        return encripta(senha, sb);
    }

    private String encripta(String senha, StringBuilder sb) {
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256"); //messageDisgest ira usar o algoritmo SHA-256
            byte[] hash = messageDigest.digest(senha.getBytes());   //transforma a senha em um array de bytes e digest calcula o hash
            return Base64.getEncoder().encodeToString(hash);    //converte o array de bytes hash em uma String usando codificação Base64
        } catch (NoSuchAlgorithmException e) {
            sb.append("Erro na encriptação da senha. ");
            return senha;
        }
    }
}
