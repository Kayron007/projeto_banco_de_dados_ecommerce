package com.mycompany.ecommercebd.control;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycompany.ecommercebd.model.CarrinhoItem;
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
            return "redirect:/";
        }
        model.addAttribute("cliente", c);
        return "minhaConta";
    }

    @GetMapping("/masculino")
    public String masculino(Model model) {
        carregarProdutosPorSexo(model, "masculino", "produtos");
        return "masculino";
    }

    @GetMapping("/feminino")
    public String feminino(Model model) {
        carregarProdutosPorSexo(model, "feminino", "produtos");
        return "feminino";
    }
    
    @GetMapping("/acessorios")
    public String acessorios(Model model) {
        carregarProdutosPorCategoria(model, "acessorio", "produtos");
        return "acessorios";
    }

    @GetMapping("/promocoes")
    public String promocoes(){
        return "promocoes";
    }

    @GetMapping("/carrinho")
    public String carrinho(HttpSession session, Model model) {
        Cliente logado = (Cliente) session.getAttribute("clienteLogado");

        if (logado == null) {
            session.setAttribute("redirectAfterLogin", "/carrinho");
            model.addAttribute("loginRequired", true);
            return "index";
        }
        List<CarrinhoItem> carrinho = obterCarrinho(session);
        double total = carrinho.stream().mapToDouble(ci -> ci.getProduto().getPreco() * ci.getQuantidade()).sum();

        model.addAttribute("clienteLogado", logado);
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalCarrinho", total);
        return "carrinho";
    }

    @PostMapping("/carrinho/adicionar")
    public String adicionarAoCarrinho(@RequestParam("produtoId") Long produtoId, HttpSession session, Model model) {
        List<CarrinhoItem> carrinho = obterCarrinho(session);

        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            Produto produto = produtoDAO.buscarPorId(produtoId);
            if (produto != null) {
                CarrinhoItem existente = carrinho.stream()
                        .filter(ci -> ci.getProduto().getId().equals(produtoId))
                        .findFirst()
                        .orElse(null);
                if (existente != null) {
                    existente.setQuantidade(existente.getQuantidade() + 1);
                } else {
                    carrinho.add(new CarrinhoItem(produto, 1));
                }
            } else {
                model.addAttribute("erro", "Produto não encontrado.");
            }
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao adicionar produto: " + e.getMessage());
        }

        session.setAttribute("carrinho", carrinho);
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/remover")
    public String removerDoCarrinho(@RequestParam("produtoId") Long produtoId, HttpSession session) {
        List<CarrinhoItem> carrinho = obterCarrinho(session);
        carrinho.removeIf(ci -> ci.getProduto().getId().equals(produtoId));
        session.setAttribute("carrinho", carrinho);
        return "redirect:/carrinho";
    }

    @GetMapping("/produtos")
    public String produtos(Model model){
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            List<Produto> todos = produtoDAO.listar();

            model.addAttribute("produtosMasculinos", filtrarPorSexo(todos, "masculino"));
            model.addAttribute("produtosFemininos", filtrarPorSexo(todos, "feminino"));
            model.addAttribute("produtosAcessorios", filtrarPorCategoria(todos, "acessorio"));
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar produtos: " + e.getMessage());
        }
        return "produtos";
    }
    
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model){
        List<CarrinhoItem> carrinho = obterCarrinho(session);
        double total = carrinho.stream().mapToDouble(ci -> ci.getProduto().getPreco() * ci.getQuantidade()).sum();
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalCarrinho", total);
        return "checkout";
    }

    /**
     * Filtra produtos pelo SEXO (case-insensitive)
     * Aceita valores como "Masculino", "masculino", "Feminino", "feminino"
     */
    private List<Produto> filtrarPorSexo(List<Produto> produtos, String sexoAlvo) {
        if (produtos == null || sexoAlvo == null) return List.of();

        final String alvo = sexoAlvo.toLowerCase();

        return produtos.stream()
                .filter(p -> {
                    String sexo = p.getSexo();
                    if (sexo == null) return false;
                    return sexo.toLowerCase().contains(alvo);
                })
                .collect(Collectors.toList());
    }

    /**
     * Filtra produtos pela CATEGORIA (acessorio, calcado, roupa, etc)
     */
    private List<Produto> filtrarPorCategoria(List<Produto> produtos, String categoriaAlvo) {
        if (produtos == null || categoriaAlvo == null) return List.of();

        final String alvo = categoriaAlvo.toLowerCase();

        return produtos.stream()
                .filter(p -> {
                    String cat = p.getCategoria();
                    if (cat == null) return false;
                    return cat.toLowerCase().contains(alvo);
                })
                .collect(Collectors.toList());
    }

    /**
     * Carrega produtos por SEXO
     */
    private void carregarProdutosPorSexo(Model model, String sexo, String atributo) {
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            model.addAttribute(atributo, filtrarPorSexo(produtoDAO.listar(), sexo));
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar produtos: " + e.getMessage());
            model.addAttribute(atributo, List.of());
        }
    }

    /**
     * Carrega produtos por CATEGORIA
     */
    private void carregarProdutosPorCategoria(Model model, String categoria, String atributo) {
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            model.addAttribute(atributo, filtrarPorCategoria(produtoDAO.listar(), categoria));
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar produtos: " + e.getMessage());
            model.addAttribute(atributo, List.of());
        }
    }

    private List<CarrinhoItem> obterCarrinho(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CarrinhoItem> carrinho = (List<CarrinhoItem>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new ArrayList<>();
            session.setAttribute("carrinho", carrinho);
        }
        return carrinho;
    }
}
