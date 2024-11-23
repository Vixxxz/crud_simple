package controle;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ControleCliente", urlPatterns = "/controlecliente")
public class ClienteControle extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("GET feito em /controlecliente");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCodificacao(req, resp);
        Gson gson = new Gson();
        StringBuilder erros = new StringBuilder();

        try {
            JsonObject jsonObject = lerJsonComoObjeto(req);
            if (!jsonObject.has("Cliente") || !jsonObject.has("ClienteEndereco")) {
                enviarRespostaErro(resp, HttpServletResponse.SC_BAD_REQUEST, "JSON inválido: Campos obrigatórios ausentes.");
                return;
            }

            List<EntidadeDominio> entidadesParaSalvar = extrairEntidades(jsonObject, gson);
            IFachada fachada = new Fachada();

            try {
                fachada.salvar(entidadesParaSalvar, erros);
                enviarRespostaSucesso(resp, entidadesParaSalvar);
            } catch (Exception e) {
                e.printStackTrace();
                enviarRespostaErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao salvar cliente e cliente endereco: " + e.getMessage());
            }
        } catch (JsonSyntaxException e) {
            enviarRespostaErro(resp, HttpServletResponse.SC_BAD_REQUEST, "Erro ao processar JSON: " + e.getMessage());
        } catch (Exception e) {
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
        List<EntidadeDominio> entidadesParaSalvar = new ArrayList<>();

        // Extrai a informação do campo "Cliente" do JSON e converte para um objeto do tipo Cliente.
        // gson.fromJson: converte JSON para um objeto Java. Aqui, é passado o JSON do campo "Cliente".
        Cliente cliente = gson.fromJson(jsonObject.get("Cliente"), Cliente.class);

        entidadesParaSalvar.add(cliente);

        // Define o tipo genérico que será usado para interpretar uma lista de objetos do tipo ClienteEndereco.
        // TypeToken é necessário para capturar tipos genéricos, como List<ClienteEndereco>, durante a desserialização.
        Type clienteEnderecoListType = new TypeToken<List<ClienteEndereco>>() {}.getType();

        // Extrai a informação do campo "ClienteEndereco" do JSON e converte para uma lista de objetos do tipo ClienteEndereco.
        // gson.fromJson: neste caso, o metodo utiliza o tipo definido (clienteEnderecoListType) para interpretar corretamente o JSON.
        List<ClienteEndereco> clienteEnderecos = gson.fromJson(jsonObject.get("ClienteEndereco"), clienteEnderecoListType);

        // Adiciona todos os objetos da lista ClienteEndereco na lista de entidades.
        entidadesParaSalvar.addAll(clienteEnderecos);

        return entidadesParaSalvar;
    }


    private void enviarRespostaSucesso(HttpServletResponse resp, Object dados) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        JsonObject resposta = new JsonObject();
        resposta.addProperty("mensagem", "Cliente e Cliente Endereço salvo com sucesso!");
        resposta.add("dados", new Gson().toJsonTree(dados));
        resp.getWriter().write(resposta.toString());
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