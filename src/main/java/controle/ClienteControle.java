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

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("GET feito em /cliente");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
        setRequestResponseEncoding(req, resp);
        StringBuilder erros = new StringBuilder();
        Gson gson = new Gson();

        try {
            // Converte a string JSON em um objeto JsonObject
            JsonObject jsonObject = JsonParser.parseString(lerJson(req)).getAsJsonObject();
            List<EntidadeDominio> salvarEntidades = extrairEntidade(jsonObject, gson);

            IFachada fachada = new Fachada();
            try (PrintWriter writer = resp.getWriter()) {
                fachada.salvar(salvarEntidades, erros);
                resp.setStatus(HttpServletResponse.SC_OK);
                writer.write(gson.toJson("Cliente e Cliente Endereço salvo com sucesso: " + salvarEntidades));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Erro: " + e.getMessage());
        }
    }

    private String lerJson(HttpServletRequest req) throws IOException {
        StringBuilder leitorJson = new StringBuilder();
        String linha;
        try (BufferedReader reader = req.getReader()) {
            while ((linha = reader.readLine()) != null) {
                leitorJson.append(linha);
            }
        }
        return leitorJson.toString();
    }

    private void setRequestResponseEncoding(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private List<EntidadeDominio> extrairEntidade(JsonObject jsonObject, Gson gson) {
        // Extrai a lista de ClienteEndereco
        //Porque não fazer: List<ClienteEndereco> lista = gson.fromJson(json, List<ClienteEndereco>.class);
        //O tipo definido da lista, <ClienteEndereco>, é genérico
        //isso quer dizer que: Em tempo de execução a List<ClienteEndereco> vai virar apenas List
        //Como o java apaga os tipos genericos em tempo de execução, não é possível desserializar a classe ClienteEndereco pois não tem nenhuma referencia
        //TypeToken permite capturar e armazenar informações do tipo generico em tempo de execução
        //E precisa ser passado ao construtor do TypeToken o tipo genérico que deseja capturar
        //Nesse caso, é passado List<ClienteEndereco> pois a lista de ClienteEndereco é a que deseja capturar do JSON
        //jsonObject.get("ClienteEndereco"): extrai o elemento "ClienteEndereco" do jsonObject
        //gson.fromJson(..., clienteEnderecoListType): O metodo fromJson do Gson é usado para desserializar JSON em objetos Java.
        //A importância de usar clienteEnderecoListType no fromJson é garantir que o Gson saiba exatamente que tipo de objeto está desserializando.
        List<EntidadeDominio> salvarEntidades = new ArrayList<>();
        Cliente cliente = gson.fromJson(jsonObject.get("Cliente"), Cliente.class);
        salvarEntidades.add(cliente);
        Type clienteEnderecoListType = new TypeToken<List<ClienteEndereco>>() {}.getType();
        salvarEntidades.addAll(gson.fromJson(jsonObject.get("ClienteEndereco"), clienteEnderecoListType));
        return salvarEntidades;
    }
}