package controle;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dominio.Bandeira;
import dominio.Cartao;
import dominio.Cliente;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "Controle Cartao", urlPatterns = "/controlecartao")
public class CartaoControle extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCodificacao(req, resp);
        Gson gson = new Gson();

        try {
            IFachada fachada = new Fachada();
            Cartao filtroCartao = new Cartao();
            Cliente filtroCliente = new Cliente();
            Bandeira filtroBandeira = new Bandeira();

            String idCartao = req.getParameter("idCartao");
            String idCliente = req.getParameter("idCliente");
            String idBandeira = req.getParameter("idBandeira");
            String numeroCartao = req.getParameter("numeroCartao");
            String nomeCliente = req.getParameter("nomeCliente");

            if (idCartao != null && !idCartao.isBlank()) {
                filtroCartao.setId(Integer.parseInt(idCartao));
            }
            if (numeroCartao != null && !numeroCartao.isBlank()) {
                filtroCartao.setNumero(numeroCartao);
            }

            if (idCliente != null && !idCliente.isBlank()) {
                filtroCliente.setId(Integer.parseInt(idCliente));
            }
            if (nomeCliente != null && !nomeCliente.isBlank()) {
                filtroCliente.setNome(nomeCliente);
            }
            filtroCartao.setCliente(filtroCliente);

            if (idBandeira != null && !idBandeira.isBlank()) {
                filtroBandeira.setId(Integer.parseInt(idBandeira));
            }
            filtroCartao.setBandeira(filtroBandeira);

            List<EntidadeDominio> resultados = fachada.consultar(filtroCartao);

            if (resultados.isEmpty()) {
                enviarRespostaErro(resp, HttpServletResponse.SC_NOT_FOUND, "Nenhum cartão encontrado com os filtros fornecidos.");
            } else {
                enviarRespostaSucesso(resp, "Consulta realizada com sucesso!", resultados);
            }
        } catch (NumberFormatException e) {
            enviarRespostaErro(resp, HttpServletResponse.SC_BAD_REQUEST, "Erro nos parâmetros: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            enviarRespostaErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro inesperado: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        configurarCodificacao(req, resp);
        Gson gson = new Gson();
        StringBuilder erros = new StringBuilder();

        try{
            JsonObject jsonObject = lerJsonComoObjeto(req);
            if (!jsonObject.has("Cartao") || !jsonObject.has("Cliente") || !jsonObject.has("Bandeira")) {
                enviarRespostaErro(resp, HttpServletResponse.SC_BAD_REQUEST, "JSON inválido: Campos obrigatórios ausentes.");
                return;
            }
            List<EntidadeDominio> entidadesParaSalvar = extrairEntidades(jsonObject, gson);
            IFachada fachada = new Fachada();

            try{
                fachada.salvar(entidadesParaSalvar, erros);
                enviarRespostaSucesso(resp, "Cartao salvo com sucesso!", entidadesParaSalvar);
            }catch(Exception e){
                e.printStackTrace();
                enviarRespostaErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao salvar cartao: " + e.getMessage());
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
        Cartao cartao = new Cartao();
        Cliente cliente = new Cliente();
        Bandeira bandeira = new Bandeira();
        if(jsonObject.has("Cartao")){
            // Extrai a informação do campo "Bandeira" do JSON e converte para um objeto do tipo Bandeira.
            // gson.fromJson: converte JSON para um objeto Java. Aqui, é passado o JSON do campo "Bandeira".
            cartao = gson.fromJson(jsonObject.get("Cartao"), Cartao.class);
        }
        if(jsonObject.has("Cliente")){
            cliente = gson.fromJson(jsonObject.get("Cliente"), Cliente.class);
        }
        if(jsonObject.has("Bandeira")){
            bandeira = gson.fromJson(jsonObject.get("Bandeira"), Bandeira.class);
        }

        cartao.setCliente(cliente);
        cartao.setBandeira(bandeira);

        entidadesParaSalvar.add(cartao);

        return entidadesParaSalvar;
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