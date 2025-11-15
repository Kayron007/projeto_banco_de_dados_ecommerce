package com.mycompany.ecommercebd.control;

import java.sql.Connection;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycompany.ecommercebd.model.Cliente;
import com.mycompany.ecommercebd.model.Conexao;
import com.mycompany.ecommercebd.model.DAO.ClienteDAO;

import jakarta.servlet.http.HttpSession;

@Controller
public class ClienteControl {

    // ------------------------------------------------------------------
    // TELAS
    // ------------------------------------------------------------------

    @GetMapping("/cadastro")
    public String abrirCadastro() {
        return "cadastro";
    }

    @GetMapping("/login")
    public String abrirLogin() {
        return "login";
    }

    // ------------------------------------------------------------------
    // CADASTRO NORMAL
    // ------------------------------------------------------------------

    @PostMapping("/cadastro")
    public String cadastroCliente(
            @RequestParam("tipo") String tipo,
            @RequestParam("nome") String nome,
            @RequestParam("email") String email,
            @RequestParam("senha") String senha,
            @RequestParam("cep") String cep,
            @RequestParam("cidade") String cidade,
            @RequestParam("logradouro") String logradouro,
            @RequestParam("numero") String numero,
            @RequestParam("bairro") String bairro,
            @RequestParam("estado") String estado,
            Model model) {

        try (Connection con = Conexao.conectar()) {

            ClienteDAO dao = new ClienteDAO(con);

            // Verifica se o email já existe
            if (dao.buscarPorEmail(email) != null) {
                model.addAttribute("mensagem", "Email já cadastrado!");
                return "resultadoinvalido";
            }

            Cliente novo = new Cliente();
            novo.setTipo(tipo);
            novo.setNome(nome);
            novo.setEmail(email);
            novo.setSenha(senha);
            novo.setCep(cep);
            novo.setCidade(cidade);
            novo.setLogradouro(logradouro);
            novo.setNumero(numero);
            novo.setBairro(bairro);
            novo.setEstado(estado);

            dao.inserir(novo);

            model.addAttribute("email", email);
            return "resultado";

        } catch (Exception e) {
            model.addAttribute("mensagem", "Erro ao cadastrar: " + e.getMessage());
            return "resultadoinvalido";
        }
    }

    // ------------------------------------------------------------------
    // LOGIN
    // ------------------------------------------------------------------

    @PostMapping("/login")
    public String loginCliente(
            @RequestParam("email") String email,
            @RequestParam("senha") String senha,
            Model model,
            HttpSession session) {

        try (Connection con = Conexao.conectar()) {

            ClienteDAO dao = new ClienteDAO(con);
            Cliente c = dao.buscarPorEmail(email);

            if (c == null || c.getSenha() == null || !c.getSenha().equals(senha)) {
                model.addAttribute("erro", "Email ou senha inválidos!");
                return "login";
            }

            // 👉 Guarda o cliente na sessão
            session.setAttribute("clienteLogado", c);

            return "redirect:/";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao logar: " + e.getMessage());
            return "login";
        }
    }
}