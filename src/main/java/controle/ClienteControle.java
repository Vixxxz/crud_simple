package controle;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dominio.Cliente;
import dominio.ClienteEndereco;
import dominio.EntidadeDominio;
import fachada.Fachada;
import fachada.IFachada;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCodificacao(req, resp);
        Gson gson = new Gson();

        try {
            IFachada fachada = new Fachada();
            JsonObject jsonObject;

            try {
                jsonObject = lerJsonComoObjeto(req); // Tenta ler o JSON do corpo
            } catch (IllegalArgumentException e) {
                // Se não houver JSON, tratamos como uma consulta sem filtros
                jsonObject = null;
            }

            // Extrai entidades se o JSON foi fornecido
            List<EntidadeDominio> entidades = jsonObject != null ? extrairEntidades(jsonObject, gson) : new ArrayList<>();

            // Consulta pelo Fachada; se nenhuma entidade for fornecida, busca todas
            List<EntidadeDominio> resultados = fachada.consultar(entidades.isEmpty() ? new Cliente() : entidades.getFirst());

            if (resultados.isEmpty()) {
                enviarRespostaErro(resp, HttpServletResponse.SC_NOT_FOUND, "Nenhum resultado encontrado.");
            } else {
                enviarRespostaSucesso(resp, "Consulta realizada com sucesso!", resultados);
            }

        } catch (JsonSyntaxException e) {
            enviarRespostaErro(resp, HttpServletResponse.SC_BAD_REQUEST, "Erro ao processar JSON: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            enviarRespostaErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro inesperado: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCodificacao(req, resp);
        Gson gson = new Gson();
        StringBuilder erros = new StringBuilder();

        try {
            JsonObject jsonObject = lerJsonComoObjeto(req);

            // Valida a presença de campos obrigatórios no JSON
            if (!jsonObject.has("Cliente") || !jsonObject.has("ClienteEndereco")) {
                enviarRespostaErro(resp, HttpServletResponse.SC_BAD_REQUEST, "JSON inválido: Campos obrigatórios ausentes.");
                return;
            }

            List<EntidadeDominio> entidadesParaSalvar = extrairEntidades(jsonObject, gson);
            IFachada fachada = new Fachada();

            try {
                fachada.salvar(entidadesParaSalvar, erros);
                enviarRespostaSucesso(resp, "Cliente e Cliente Endereço salvo com sucesso!", entidadesParaSalvar);
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
        if (json.isBlank()) {
            throw new IllegalArgumentException("Nenhum JSON foi fornecido na requisição.");
        }
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
        if (jsonObject.has("Cliente")) {
            Cliente cliente = gson.fromJson(jsonObject.get("Cliente"), Cliente.class);
            entidades.add(cliente);
        }
        if (jsonObject.has("ClienteEndereco")) {
            Type clienteEnderecoListType = new TypeToken<List<ClienteEndereco>>() {}.getType();
            List<ClienteEndereco> clienteEnderecos = gson.fromJson(jsonObject.get("ClienteEndereco"), clienteEnderecoListType);
            entidades.addAll(clienteEnderecos);
        }
        return entidades;
    }

    private void enviarRespostaSucesso(HttpServletResponse resp, String mensagem, Object dados) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);

        JsonObject resposta = new JsonObject();
        resposta.addProperty("mensagem", mensagem);
        resposta.add("dados", new Gson().toJsonTree(dados));

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(resposta.toString());
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