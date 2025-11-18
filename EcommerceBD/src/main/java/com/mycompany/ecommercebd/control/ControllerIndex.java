package com.mycompany.ecommercebd.control;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestHeader;

import com.mycompany.ecommercebd.model.Cliente;
import com.mycompany.ecommercebd.model.Conexao;
import com.mycompany.ecommercebd.model.DAO.ProdutoDAO;
import com.mycompany.ecommercebd.model.Pedido;
import com.mycompany.ecommercebd.model.PedidoProduto;
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

        List<PedidoProduto> carrinho = obterCarrinho(session);
        BigDecimal total = carrinho.stream()
                .map(pp -> pp.getPrecoUnitario().multiply(BigDecimal.valueOf(pp.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("clienteLogado", logado);
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalCarrinho", total);
        model.addAttribute("totalCarrinhoFormatado", String.format("%.2f", total));
        return "carrinho";
    }

    @PostMapping("/carrinho/adicionar")
    public String adicionarAoCarrinho(
            @RequestParam("produtoId") Long produtoId,
            @RequestHeader(value = "Referer", required = false) String referer,
            HttpSession session, 
            Model model) {
        
        List<PedidoProduto> carrinho = obterCarrinho(session);

        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            Produto produto = produtoDAO.buscarPorId(produtoId);
            
            if (produto != null) {
                // Verifica se o produto já está no carrinho
                PedidoProduto existente = carrinho.stream()
                        .filter(pp -> pp.getProduto().getId().equals(produtoId))
                        .findFirst()
                        .orElse(null);
                
                if (existente != null) {
                    // Incrementa a quantidade
                    existente.setQuantidade(existente.getQuantidade() + 1);
                } else {
                    // Cria pedido temporário
                    Pedido pedidoTemp = obterPedidoTemporario(session);
                    
                    // Cria novo item no carrinho
                    PedidoProduto novoProduto = new PedidoProduto(
                        pedidoTemp,
                        produto,
                        1,
                        BigDecimal.valueOf(produto.getPreco())
                    );
                    carrinho.add(novoProduto);
                }
                
                session.setAttribute("mensagemSucesso", "Produto adicionado ao carrinho!");
            } else {
                model.addAttribute("erro", "Produto não encontrado.");
            }
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao adicionar produto: " + e.getMessage());
            e.printStackTrace();
        }

        session.setAttribute("carrinho", carrinho);
        
        // Redireciona para a página anterior, ou para home se não houver referer
        return "redirect:" + (referer != null ? referer : "/");
    }

    @PostMapping("/carrinho/remover")
    public String removerDoCarrinho(@RequestParam("produtoId") Long produtoId, HttpSession session) {
        List<PedidoProduto> carrinho = obterCarrinho(session);
        carrinho.removeIf(pp -> pp.getProduto().getId().equals(produtoId));
        session.setAttribute("carrinho", carrinho);
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/atualizar")
    public String atualizarQuantidade(
            @RequestParam("produtoId") Long produtoId,
            @RequestParam("quantidade") int quantidade,
            HttpSession session) {
        
        List<PedidoProduto> carrinho = obterCarrinho(session);
        
        PedidoProduto item = carrinho.stream()
                .filter(pp -> pp.getProduto().getId().equals(produtoId))
                .findFirst()
                .orElse(null);
        
        if (item != null) {
            if (quantidade > 0) {
                item.setQuantidade(quantidade);
            } else {
                carrinho.remove(item);
            }
        }
        
        session.setAttribute("carrinho", carrinho);
        return "redirect:/carrinho";
    }

    @GetMapping("/produtos/todos")
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
        Cliente logado = (Cliente) session.getAttribute("clienteLogado");
        
        if (logado == null) {
            return "redirect:/";
        }

        List<PedidoProduto> carrinho = obterCarrinho(session);
        
        if (carrinho.isEmpty()) {
            model.addAttribute("erro", "Carrinho vazio!");
            return "redirect:/carrinho";
        }
        
        BigDecimal total = carrinho.stream()
                .map(pp -> pp.getPrecoUnitario().multiply(BigDecimal.valueOf(pp.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        model.addAttribute("clienteLogado", logado);
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalCarrinho", total);
        model.addAttribute("totalCarrinhoFormatado", String.format("%.2f", total));
        return "checkout";
    }

    /**
     * Filtra produtos pelo SEXO (case-insensitive)
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
     * Filtra produtos pela CATEGORIA
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

    /**
     * Obtém o carrinho da sessão (lista de PedidoProduto)
     */
    private List<PedidoProduto> obterCarrinho(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<PedidoProduto> carrinho = (List<PedidoProduto>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new ArrayList<>();
            session.setAttribute("carrinho", carrinho);
        }
        return carrinho;
    }

    /**
     * Cria pedido temporário para o carrinho
     * O pedido só existe na sessão e é perdido ao deslogar
     */
    private Pedido obterPedidoTemporario(HttpSession session) {
        Pedido pedidoTemp = (Pedido) session.getAttribute("pedidoTemporario");
        
        if (pedidoTemp == null) {
            pedidoTemp = new Pedido();
            pedidoTemp.setId(-1L);
            pedidoTemp.setStatus("CARRINHO");
            pedidoTemp.setData(java.time.LocalDateTime.now());
            pedidoTemp.setValorTotal(BigDecimal.ZERO);
            session.setAttribute("pedidoTemporario", pedidoTemp);
        }
        
        return pedidoTemp;
    }
}