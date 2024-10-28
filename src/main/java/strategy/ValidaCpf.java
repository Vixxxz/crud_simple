package strategy;

import dominio.Cliente;
import dominio.EntidadeDominio;

public class ValidaCpf implements IStrategy{
    @Override
    public String processar(EntidadeDominio entidade, StringBuilder sb) {
        Cliente cliente = (Cliente) entidade;
        String cpf = cliente.getCpf();
        verificaCpf(cpf, sb);
        return null;
    }

    private void verificaCpf(String cpf, StringBuilder sb) {
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}") || !cpf.matches("\\d+")){
            sb.append("CPF Invalido!");
            return;
        }
        int penultimoNumCpf = Character.getNumericValue(cpf.charAt(9));
        int ultimoNumCpf = Character.getNumericValue(cpf.charAt(10));
        int dv1 = calculaDv1(cpf);
        int dv2 = calculaDv2(cpf, dv1);

        if(penultimoNumCpf != dv1 || ultimoNumCpf != dv2){
            sb.append("CPF Invalido!");
        }
    }

    private int calculaDv2(String cpf, int dv1) {
        int numCpf, dv2 = 0;
        for (int i = 0; i <= 8; i++) {
            numCpf = Character.getNumericValue(cpf.charAt(i));
            dv2 += numCpf * (11 - i);
        }
        dv2 += dv1 * 2;
        dv2 = dv2 % 11;
        if (dv2 == 1 || dv2 == 0)
        {
            return 0;
        }
        else
        {
            return 11 - dv2;
        }
    }

    private int calculaDv1(String cpf) {
        int numCpf, dv1 = 0;
        for (int i = 0; i <= 8; i++)
        {
            numCpf = Character.getNumericValue(cpf.charAt(i));
            dv1 += numCpf * (10 - i);
        }
        dv1 = dv1 % 11;
        if (dv1 == 1 || dv1 == 0)
        {
            return 0;
        }
        else
        {
            return 11 - dv1;
        }
    }
}
