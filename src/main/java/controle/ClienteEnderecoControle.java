package controle;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dominio.Cliente;
import dominio.ClienteEndereco;
import dominio.EntidadeDominio;
import fachada.Fachada;
import fachada.IFachada;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "Controle Cliente Endereco", urlPatterns = "/controleclienteendereco")
public class ClienteEnderecoControle extends HttpServlet {
    private static final Long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        configurarCodificacao(req, resp);
        Gson gson = new Gson();

        try {
            IFachada fachada = new Fachada();
            JsonObject jsonObject = lerJsonComoObjeto(req);
            List<EntidadeDominio> entidades = extrairEntidades(jsonObject, gson);

            if (entidades.isEmpty()) {
                enviarRespostaErro(resp, HttpServletResponse.SC_BAD_REQUEST, "Nenhuma entidade fornecida para consulta.");
                return;
            }

            List<EntidadeDominio> resultados = fachada.consultar(entidades.getFirst());

            enviarRespostaSucesso(resp, "Consulta realizada com sucesso!", resultados);

        } catch (JsonSyntaxException e) {
            // JSON inválido ou mal formatado
            enviarRespostaErro(resp, HttpServletResponse.SC_BAD_REQUEST, "Erro ao processar JSON: " + e.getMessage());
        } catch (Exception e) {
            // Outros erros inesperados
            e.printStackTrace();
            enviarRespostaErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro inesperado: " + e.getMessage());
        }
    }

    private JsonObject lerJsonComoObjeto(HttpServletRequest req) throws IOException {
        String json = lerJsonComoString(req);
        return JsonParser.parseString(json).getAsJsonObject();
    }

    private String lerJsonComoString(HttpServletRequest req) throws IOException {
        StringBuilder leitorJson = new StringBuilder();
        String linha;
        try (BufferedReader reader = req.getReader()) {
            while ((linha = reader.readLine()) != null) {
                leitorJson.append(linha);
            }
        }
        return leitorJson.toString();
    }

    private void configurarCodificacao(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private List<EntidadeDominio> extrairEntidades(JsonObject jsonObject, Gson gson) {
        List<EntidadeDominio> entidades = new ArrayList<>();
        if(jsonObject.has("Cliente")){
            Cliente cli = gson.fromJson(jsonObject.get("Cliente"), Cliente.class);
            ClienteEndereco cliEnd = new ClienteEndereco();
            cliEnd.setCliente(cli);
            entidades.add(cliEnd);
        }
        return entidades;
    }


    private void enviarRespostaSucesso(HttpServletResponse resp, String mensagem, Object dados) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK); // Define o status HTTP como 200 (OK)

        JsonObject resposta = new JsonObject();
        resposta.addProperty("mensagem", mensagem); // Adiciona a mensagem fornecida como parâmetro
        resposta.add("dados", new Gson().toJsonTree(dados)); // Converte os dados fornecidos para JSON

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(resposta.toString()); // Escreve a resposta no fluxo de saída
        }
    }

    private void enviarRespostaErroValidacao(HttpServletResponse resp, String errosDeValidacao) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        JsonObject resposta = new JsonObject();
        resposta.addProperty("errosDeValidacao", errosDeValidacao);
        resp.getWriter().write(resposta.toString());
    }

    private void enviarRespostaErro(HttpServletResponse resp, int codigoStatus, String mensagemErro) throws IOException {
        resp.setStatus(codigoStatus);
        JsonObject resposta = new JsonObject();
        resposta.addProperty("erro", mensagemErro);
        resp.getWriter().write(resposta.toString());
    }
}
