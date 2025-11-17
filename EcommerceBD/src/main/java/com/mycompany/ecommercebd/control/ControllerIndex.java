/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommercebd.control;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.mycompany.ecommercebd.model.Cliente;
import com.mycompany.ecommercebd.model.Conexao;
import com.mycompany.ecommercebd.model.DAO.ProdutoDAO;
import com.mycompany.ecommercebd.model.Produto;

import jakarta.servlet.http.HttpSession;

@Controller 
public class ControllerIndex {
    
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        Cliente logado = (Cliente) session.getAttribute("clienteLogado");
        model.addAttribute("clienteLogado", logado);
        return "index";
    }

    @ModelAttribute("clienteLogado")
    public Cliente addClienteLogadoToModel(HttpSession session) {
        return (Cliente) session.getAttribute("clienteLogado");
    }

    @GetMapping("/minhaConta")
    public String minhaConta(HttpSession session, Model model) {
        Cliente c = (Cliente) session.getAttribute("clienteLogado");

        if (c == null) {
            return "redirect:/"; // ou volta para login
        }
        model.addAttribute("cliente", c);
        return "minhaConta";
    }

   @GetMapping("/masculino")
    public String masculino(Model model) {
        carregarProdutosPorCategoria(model, "masculino", "produtos");
        return "masculino";   // carrega templates/masculino.html
    }

    @GetMapping("/feminino")
    public String feminino(Model model) {
        carregarProdutosPorCategoria(model, "feminino", "produtos");
        return "feminino";    // carrega templates/feminino.html
    }
    
   @GetMapping("/acessorios")
    public String acessorios(Model model) {
        carregarProdutosPorCategoria(model, "acessorio", "produtos");
        return "acessorios"; // carrega templates/acessorios.html
}

    
    @GetMapping("/promocoes")
    public String promocoes(){
        return "promocoes";
    }

   @GetMapping("/carrinho")
    public String carrinho(HttpSession session, Model model) {
        Cliente logado = (Cliente) session.getAttribute("clienteLogado");

        if (logado == null) {
            // Usuário não logado → renderiza a página atual com modal aberto
            session.setAttribute("redirectAfterLogin", "/carrinho");
            model.addAttribute("loginRequired", true);
            return "index"; // fallback
        }
        model.addAttribute("clienteLogado", logado);
        return "carrinho";
    }

    @GetMapping("/produtos")
    public String produtos(Model model){
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            List<Produto> todos = produtoDAO.listar();

            model.addAttribute("produtosMasculinos", filtrarCategoria(todos, "masculino"));
            model.addAttribute("produtosFemininos", filtrarCategoria(todos, "feminino"));
            model.addAttribute("produtosAcessorios", filtrarCategoria(todos, "acessorio"));
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar produtos: " + e.getMessage());
        }
        return "produtos";
    }
    
    @GetMapping("/checkout")
    public String checkout(){
        return "checkout";
         
    }

    private List<Produto> filtrarCategoria(List<Produto> produtos, String categoriaAlvo) {
        if (produtos == null) return List.of();
        return produtos.stream()
                .filter(p -> p.getCategoria() != null &&
                        p.getCategoria().toLowerCase().contains(categoriaAlvo.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void carregarProdutosPorCategoria(Model model, String categoria, String atributo) {
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            model.addAttribute(atributo, filtrarCategoria(produtoDAO.listar(), categoria));
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar produtos: " + e.getMessage());
            model.addAttribute(atributo, List.of());
        }
    }
}
