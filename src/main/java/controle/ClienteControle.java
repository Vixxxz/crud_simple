package controle;

import com.google.gson.Gson;
import dominio.Cliente;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "ControleCliente", urlPatterns = "/controlecliente")
public class ClienteControle extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("GET feito em /cliente");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        StringBuilder leitorJson = new StringBuilder();
        String linha;

        try(BufferedReader reader = req.getReader()) {
            while((linha = reader.readLine()) != null){
                leitorJson.append(linha);
            }
        }

        Gson gson = new Gson();
        Cliente cliente = gson.fromJson(leitorJson.toString(), Cliente.class);

        IFachada fachada = new Fachada();

        try{
            StringBuilder erros = new StringBuilder();
            fachada.salvar(cliente, erros);
            resp.getWriter().write(gson.toJson("Cliente salvo com sucesso!"));
        } catch (Exception e) {
            throw new RuntimeException("Erro: " + e.getMessage());
        }

        System.out.println(cliente.toString());
    }
}