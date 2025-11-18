package com.mycompany.ecommercebd.control;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
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

// Indica que esta classe Ã© um Controller do Spring MVC, responsÃ¡vel por gerenciar as principais requisiÃ§Ãµes do sistema
@Controller 
public class ControllerIndex {
    
    // ------------------------------------------------------------------
    // NAVEGAÃ‡ÃƒO PRINCIPAL
    // ------------------------------------------------------------------
    
    // Mapeia requisiÃ§Ãµes GET para "/" (pÃ¡gina inicial)
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        // Recupera o cliente logado da sessÃ£o para exibir informaÃ§Ãµes personalizadas
        Cliente logado = (Cliente) session.getAttribute("clienteLogado");
        model.addAttribute("clienteLogado", logado);
        // Retorna o template da pÃ¡gina inicial
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            List<Produto> todos = produtoDAO.listar();
            model.addAttribute("promocoes", selecionarNovidades(todos, 4));
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar novidades: " + e.getMessage());
            model.addAttribute("promocoes", List.of());
        }
        return "index";
    }

    // @ModelAttribute torna o clienteLogado disponÃ­vel em TODAS as views automaticamente
    // Evita ter que adicionar manualmente em cada mÃ©todo do controller
    @ModelAttribute("clienteLogado")
    public Cliente addClienteLogadoToModel(HttpSession session) {
        return (Cliente) session.getAttribute("clienteLogado");
    }

    // Mapeia requisiÃ§Ãµes GET para "/minhaConta" - Ã¡rea do cliente
    @GetMapping("/minhaConta")
    public String minhaConta(HttpSession session, Model model) {
        // Recupera o cliente logado
        Cliente c = (Cliente) session.getAttribute("clienteLogado");

        // Se nÃ£o estiver logado, redireciona para a home
        if (c == null) {
            return "redirect:/";
        }
        
        // Adiciona o cliente ao modelo para exibir seus dados
        model.addAttribute("cliente", c);
        return "minhaConta";
    }

    // ------------------------------------------------------------------
    // PÃGINAS DE CATÃLOGO POR CATEGORIA
    // ------------------------------------------------------------------

    // Mapeia requisiÃ§Ãµes GET para "/masculino" - produtos masculinos
    @GetMapping("/masculino")
    public String masculino(Model model) {
        // Carrega produtos filtrados por sexo masculino
        carregarProdutosPorSexo(model, "masculino", "produtos");
        return "masculino";
    }

    // Mapeia requisiÃ§Ãµes GET para "/feminino" - produtos femininos
    @GetMapping("/feminino")
    public String feminino(Model model) {
        // Carrega produtos filtrados por sexo feminino
        carregarProdutosPorSexo(model, "feminino", "produtos");
        return "feminino";
    }
    
    // Mapeia requisiÃ§Ãµes GET para "/acessorios" - produtos da categoria acessÃ³rios
    @GetMapping("/acessorios")
    public String acessorios(Model model) {
        // Carrega produtos filtrados por categoria acessÃ³rio
        carregarProdutosPorCategoria(model, "acessorio", "produtos");
        return "acessorios";
    }

    // Mapeia requisiÃ§Ãµes GET para "/promocoes" - pÃ¡gina de promoÃ§Ãµes
    @GetMapping("/promocoes")
    public String promocoes(){
        return "promocoes";
    }

    // ------------------------------------------------------------------
    // CARRINHO DE COMPRAS
    // ------------------------------------------------------------------

    // Mapeia requisiÃ§Ãµes GET para "/carrinho" - visualizaÃ§Ã£o do carrinho
    @GetMapping("/carrinho")
    public String carrinho(HttpSession session, Model model) {
        // Recupera o cliente logado da sessÃ£o
        Cliente logado = (Cliente) session.getAttribute("clienteLogado");

        // Verifica se o usuÃ¡rio estÃ¡ logado
        if (logado == null) {
            // Salva a URL de redirecionamento para retornar apÃ³s o login
            session.setAttribute("redirectAfterLogin", "/carrinho");
            // Indica que o modal de login deve ser aberto
            model.addAttribute("loginRequired", true);
            // Redireciona para index onde o modal serÃ¡ exibido
            return "index";
        }

        // ObtÃ©m o carrinho da sessÃ£o (lista de produtos)
        List<PedidoProduto> carrinho = obterCarrinho(session);
        
        // Calcula o valor total do carrinho
        // Multiplica o preÃ§o unitÃ¡rio pela quantidade de cada item e soma tudo
        BigDecimal total = carrinho.stream()
                .map(pp -> pp.getPrecoUnitario().multiply(BigDecimal.valueOf(pp.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Adiciona dados ao modelo para exibiÃ§Ã£o na view
        model.addAttribute("clienteLogado", logado);
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalCarrinho", total);
        // Formata o total para exibiÃ§Ã£o (ex: "99.90")
        model.addAttribute("totalCarrinhoFormatado", String.format("%.2f", total));
        
        return "carrinho";
    }

    // Mapeia requisiÃ§Ãµes POST para "/carrinho/adicionar" - adiciona produto ao carrinho
    @PostMapping("/carrinho/adicionar")
    public String adicionarAoCarrinho(
            // Captura o ID do produto enviado pelo formulÃ¡rio
            @RequestParam("produtoId") Long produtoId,
            // Captura o cabeÃ§alho HTTP "Referer" para saber de qual pÃ¡gina veio a requisiÃ§Ã£o
            @RequestHeader(value = "Referer", required = false) String referer,
            // HttpSession para armazenar o carrinho
            HttpSession session, 
            // Model para passar mensagens de erro
            Model model) {
        
        // ObtÃ©m o carrinho atual da sessÃ£o
        List<PedidoProduto> carrinho = obterCarrinho(session);

        // Try-with-resources: abre conexÃ£o com banco de dados e fecha automaticamente
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            // Busca o produto no banco pelo ID
            Produto produto = produtoDAO.buscarPorId(produtoId);
            
            // Verifica se o produto existe
            if (produto != null) {
                // Verifica se o produto jÃ¡ estÃ¡ no carrinho
                PedidoProduto existente = carrinho.stream()
                        .filter(pp -> pp.getProduto().getId().equals(produtoId))
                        .findFirst()
                        .orElse(null);
                
                if (existente != null) {
                    // Se jÃ¡ existe, apenas incrementa a quantidade
                    existente.setQuantidade(existente.getQuantidade() + 1);
                } else {
                    // Se nÃ£o existe, cria um novo item no carrinho
                    // ObtÃ©m ou cria um pedido temporÃ¡rio (ainda nÃ£o persistido no banco)
                    Pedido pedidoTemp = obterPedidoTemporario(session);
                    
                    // Cria novo item associando pedido, produto, quantidade e preÃ§o
                    PedidoProduto novoProduto = new PedidoProduto(
                        pedidoTemp,
                        produto,
                        1,  // Quantidade inicial
                        BigDecimal.valueOf(produto.getPreco())
                    );
                    // Adiciona o novo item ao carrinho
                    carrinho.add(novoProduto);
                }
                
                // Adiciona mensagem de sucesso na sessÃ£o
                session.setAttribute("mensagemSucesso", "Produto adicionado ao carrinho!");
            } else {
                // Produto nÃ£o encontrado no banco
                model.addAttribute("erro", "Produto nÃ£o encontrado.");
            }
        } catch (Exception e) {
            // Captura qualquer exceÃ§Ã£o durante o processo
            model.addAttribute("erro", "Erro ao adicionar produto: " + e.getMessage());
            e.printStackTrace();
        }

        // Atualiza o carrinho na sessÃ£o
        session.setAttribute("carrinho", carrinho);
        
        // Redireciona para a pÃ¡gina anterior (referer) ou para home se nÃ£o houver
        // Isso mantÃ©m o usuÃ¡rio navegando sem ser levado ao carrinho automaticamente
        return "redirect:" + (referer != null ? referer : "/");
    }

    // Mapeia requisiÃ§Ãµes POST para "/carrinho/remover" - remove produto do carrinho
    @PostMapping("/carrinho/remover")
    public String removerDoCarrinho(
            // Captura o ID do produto a ser removido
            @RequestParam("produtoId") Long produtoId, 
            HttpSession session) {
        
        // ObtÃ©m o carrinho da sessÃ£o
        List<PedidoProduto> carrinho = obterCarrinho(session);
        
        // Remove todos os itens do carrinho que tenham o ID do produto especificado
        carrinho.removeIf(pp -> pp.getProduto().getId().equals(produtoId));
        
        // Atualiza o carrinho na sessÃ£o
        session.setAttribute("carrinho", carrinho);
        
        // Redireciona de volta para a pÃ¡gina do carrinho
        return "redirect:/carrinho";
    }

    // Mapeia requisiÃ§Ãµes POST para "/carrinho/atualizar" - atualiza quantidade de um item
    @PostMapping("/carrinho/atualizar")
    public String atualizarQuantidade(
            // Captura o ID do produto a ser atualizado
            @RequestParam("produtoId") Long produtoId,
            // Captura a nova quantidade desejada
            @RequestParam("quantidade") int quantidade,
            HttpSession session) {
        
        // ObtÃ©m o carrinho da sessÃ£o
        List<PedidoProduto> carrinho = obterCarrinho(session);
        
        // Busca o item no carrinho pelo ID do produto
        PedidoProduto item = carrinho.stream()
                .filter(pp -> pp.getProduto().getId().equals(produtoId))
                .findFirst()
                .orElse(null);
        
        // Se o item existe no carrinho
        if (item != null) {
            if (quantidade > 0) {
                // Se quantidade for positiva, atualiza
                item.setQuantidade(quantidade);
            } else {
                // Se quantidade for 0 ou negativa, remove o item
                carrinho.remove(item);
            }
        }
        
        // Atualiza o carrinho na sessÃ£o
        session.setAttribute("carrinho", carrinho);
        
        // Redireciona de volta para a pÃ¡gina do carrinho
        return "redirect:/carrinho";
    }

    // ------------------------------------------------------------------
    // CATÃLOGO COMPLETO
    // ------------------------------------------------------------------

    // Mapeia requisiÃ§Ãµes GET para "/produtos/todos" - exibe todos os produtos por categoria
    @GetMapping("/produtos/todos")
    public String produtos(Model model){
        // Try-with-resources: abre conexÃ£o com banco de dados e fecha automaticamente
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            // Busca TODOS os produtos do banco
            List<Produto> todos = produtoDAO.listar();

            // Filtra e adiciona produtos masculinos ao modelo
            model.addAttribute("produtosMasculinos", filtrarPorSexo(todos, "masculino"));
            // Filtra e adiciona produtos femininos ao modelo
            model.addAttribute("produtosFemininos", filtrarPorSexo(todos, "feminino"));
            // Filtra e adiciona acessÃ³rios ao modelo
            model.addAttribute("produtosAcessorios", filtrarPorCategoria(todos, "acessorio"));
            
        } catch (Exception e) {
            // Captura qualquer exceÃ§Ã£o ao carregar produtos
            model.addAttribute("erro", "Erro ao carregar produtos: " + e.getMessage());
        }
        
        // Retorna o template que exibe todos os produtos organizados por categoria
        return "produtos";
    }
    
    // ------------------------------------------------------------------
    // CHECKOUT (FINALIZAÃ‡ÃƒO DE COMPRA)
    // ------------------------------------------------------------------

    // Mapeia requisiÃ§Ãµes GET para "/checkout" - pÃ¡gina de finalizaÃ§Ã£o de compra
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model){
        // Recupera o cliente logado da sessÃ£o
        Cliente logado = (Cliente) session.getAttribute("clienteLogado");
        
        // Verifica se o usuÃ¡rio estÃ¡ logado (obrigatÃ³rio para finalizar compra)
        if (logado == null) {
            // Se nÃ£o estiver logado, redireciona para home
            return "redirect:/";
        }

        // ObtÃ©m o carrinho da sessÃ£o
        List<PedidoProduto> carrinho = obterCarrinho(session);
        
        // Verifica se o carrinho estÃ¡ vazio
        if (carrinho.isEmpty()) {
            model.addAttribute("erro", "Carrinho vazio!");
            // Redireciona para o carrinho se estiver vazio
            return "redirect:/carrinho";
        }
        
        // Calcula o valor total da compra
        BigDecimal total = carrinho.stream()
                .map(pp -> pp.getPrecoUnitario().multiply(BigDecimal.valueOf(pp.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Adiciona dados ao modelo para exibiÃ§Ã£o na pÃ¡gina de checkout
        model.addAttribute("clienteLogado", logado);
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalCarrinho", total);
        model.addAttribute("totalCarrinhoFormatado", String.format("%.2f", total));
        
        // Retorna o template de checkout
        return "checkout";
    }

    // Mapeia requisiÃ§Ãµes POST para "/processar-pedido" - finaliza a compra (versÃ£o temporÃ¡ria)
    @PostMapping("/processar-pedido")
    public String processarPedido(HttpSession session, Model model) {
        // Recupera o cliente logado da sessÃ£o
        Cliente logado = (Cliente) session.getAttribute("clienteLogado");
        
        // Verifica se o usuÃ¡rio estÃ¡ logado
        if (logado == null) {
            // Se nÃ£o estiver logado, redireciona para home
            return "redirect:/";
        }
        
        // ObtÃ©m o carrinho da sessÃ£o
        List<PedidoProduto> carrinho = obterCarrinho(session);
        
        // Verifica se o carrinho estÃ¡ vazio
        if (carrinho.isEmpty()) {
            model.addAttribute("erro", "Carrinho vazio!");
            return "redirect:/carrinho";
        }
        
        // Limpa o carrinho para simular compra finalizada
        session.removeAttribute("carrinho");
        session.removeAttribute("pedidoTemporario");
        
        // Adiciona mensagem de sucesso na sessÃ£o para exibir na prÃ³xima pÃ¡gina
        session.setAttribute("mensagemSucesso", "Pedido finalizado com sucesso!");
        
        // Redireciona para a pÃ¡gina inicial
        return "redirect:/";
    }

    // ------------------------------------------------------------------
    // MÉTODOS AUXILIARES DE FILTRAGEM
    // ------------------------------------------------------------------

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

    // ------------------------------------------------------------------
    // MÉTODOS AUXILIARES DE CARREGAMENTO
    // ------------------------------------------------------------------

    private void carregarProdutosPorSexo(Model model, String sexo, String atributo) {
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            model.addAttribute(atributo, filtrarPorSexo(produtoDAO.listar(), sexo));
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar produtos: " + e.getMessage());
            model.addAttribute(atributo, List.of());
        }
    }

    private void carregarProdutosPorCategoria(Model model, String categoria, String atributo) {
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            model.addAttribute(atributo, filtrarPorCategoria(produtoDAO.listar(), categoria));
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar produtos: " + e.getMessage());
            model.addAttribute(atributo, List.of());
        }
    }

    private List<Produto> selecionarNovidades(List<Produto> produtos, int limite) {
        if (produtos == null) return List.of();
        return produtos.stream()
                .filter(p -> p.getId() != null)
                .sorted(Comparator.comparing(Produto::getId).reversed())
                .limit(limite)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // MÉTODOS AUXILIARES DE GERENCIAMENTO DE CARRINHO
    // ------------------------------------------------------------------

    private List<PedidoProduto> obterCarrinho(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<PedidoProduto> carrinho = (List<PedidoProduto>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new ArrayList<>();
            session.setAttribute("carrinho", carrinho);
        }
        return carrinho;
    }

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
