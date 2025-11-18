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

// Indica que esta classe é um Controller do Spring MVC, responsável por gerenciar as principais requisições do sistema
@Controller 
public class ControllerIndex {

    /**
     * IDs configurados para exibir produtos específicos na página de promoções.
     * Se a lista estiver vazia, usaremos a categoria "promocao".
     * Atualize com os IDs desejados do banco (ex.: List.of(5L, 12L)).
     */
    private static final List<Long> IDS_PROMO_CONFIG = List.of();
    
    // ------------------------------------------------------------------
    // NAVEGAÇÃO PRINCIPAL
    // ------------------------------------------------------------------
    
    // Mapeia requisições GET para "/" (página inicial)
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        // Recupera o cliente logado da sessão para exibir informações personalizadas
        Cliente logado = (Cliente) session.getAttribute("clienteLogado");
        model.addAttribute("clienteLogado", logado);
        // Carrega produtos mais recentes para a seção "Novidades da semana"
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            List<Produto> novidades = produtoDAO.listarMaisRecentes(8);
            model.addAttribute("novidades", novidades);
        } catch (Exception e) {
            model.addAttribute("novidades", new ArrayList<Produto>());
            System.out.println("Erro ao carregar novidades: " + e.getMessage());
        }
        // Retorna o template da página inicial
        return "index";
    }

    // @ModelAttribute torna o clienteLogado disponível em TODAS as views automaticamente
    // Evita ter que adicionar manualmente em cada método do controller
    @ModelAttribute("clienteLogado")
    public Cliente addClienteLogadoToModel(HttpSession session) {
        return (Cliente) session.getAttribute("clienteLogado");
    }

    // Mapeia requisições GET para "/minhaConta" - área do cliente
    @GetMapping("/minhaConta")
    public String minhaConta(HttpSession session, Model model) {
        // Recupera o cliente logado
        Cliente c = (Cliente) session.getAttribute("clienteLogado");

        // Se não estiver logado, redireciona para a home
        if (c == null) {
            return "redirect:/";
        }
        
        // Adiciona o cliente ao modelo para exibir seus dados
        model.addAttribute("cliente", c);
        return "minhaConta";
    }

    // ------------------------------------------------------------------
    // PÁGINAS DE CATÁLOGO POR CATEGORIA
    // ------------------------------------------------------------------

    // Mapeia requisições GET para "/masculino" - produtos masculinos
    @GetMapping("/masculino")
    public String masculino(Model model) {
        // Carrega produtos filtrados por sexo masculino
        carregarProdutosPorSexo(model, "masculino", "produtos");
        return "masculino";
    }

    // Mapeia requisições GET para "/feminino" - produtos femininos
    @GetMapping("/feminino")
    public String feminino(Model model) {
        // Carrega produtos filtrados por sexo feminino
        carregarProdutosPorSexo(model, "feminino", "produtos");
        return "feminino";
    }
    
    // Mapeia requisições GET para "/acessorios" - produtos da categoria acessórios
    @GetMapping("/acessorios")
    public String acessorios(Model model) {
        // Carrega produtos filtrados por categoria acessório
        carregarProdutosPorCategoria(model, "acessorio", "produtos");
        return "acessorios";
    }

    // Mapeia requisições GET para "/promocoes" - página de promoções
    @GetMapping("/promocoes")
    public String promocoes(Model model){
        // Carrega produtos de promoções priorizando IDs específicos (ou categoria "promocao" se vazio)
        carregarProdutosPromocao(model);
        return "promocoes";
    }

    // ------------------------------------------------------------------
    // CARRINHO DE COMPRAS
    // ------------------------------------------------------------------

    // Mapeia requisições GET para "/carrinho" - visualização do carrinho
    @GetMapping("/carrinho")
    public String carrinho(HttpSession session, Model model) {
        // Recupera o cliente logado da sessão
        Cliente logado = (Cliente) session.getAttribute("clienteLogado");

        // Verifica se o usuário está logado
        if (logado == null) {
            // Salva a URL de redirecionamento para retornar após o login
            session.setAttribute("redirectAfterLogin", "/carrinho");
            // Indica que o modal de login deve ser aberto
            model.addAttribute("loginRequired", true);
            // Redireciona para index onde o modal será exibido
            return "index";
        }

        // Obtém o carrinho da sessão (lista de produtos)
        List<PedidoProduto> carrinho = obterCarrinho(session);
        
        // Calcula o valor total do carrinho
        // Multiplica o preço unitário pela quantidade de cada item e soma tudo
        BigDecimal total = carrinho.stream()
                .map(pp -> pp.getPrecoUnitario().multiply(BigDecimal.valueOf(pp.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Adiciona dados ao modelo para exibição na view
        model.addAttribute("clienteLogado", logado);
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalCarrinho", total);
        // Formata o total para exibição (ex: "99.90")
        model.addAttribute("totalCarrinhoFormatado", String.format("%.2f", total));
        
        return "carrinho";
    }

    // Mapeia requisições POST para "/carrinho/adicionar" - adiciona produto ao carrinho
    @PostMapping("/carrinho/adicionar")
    public String adicionarAoCarrinho(
            // Captura o ID do produto enviado pelo formulário
            @RequestParam("produtoId") Long produtoId,
            // Captura o cabeçalho HTTP "Referer" para saber de qual página veio a requisição
            @RequestHeader(value = "Referer", required = false) String referer,
            // HttpSession para armazenar o carrinho
            HttpSession session, 
            // Model para passar mensagens de erro
            Model model) {
        
        // Obtém o carrinho atual da sessão
        List<PedidoProduto> carrinho = obterCarrinho(session);

        // Try-with-resources: abre conexão com banco de dados e fecha automaticamente
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            // Busca o produto no banco pelo ID
            Produto produto = produtoDAO.buscarPorId(produtoId);
            
            // Verifica se o produto existe
            if (produto != null) {
                // Verifica se o produto já está no carrinho
                PedidoProduto existente = carrinho.stream()
                        .filter(pp -> pp.getProduto().getId().equals(produtoId))
                        .findFirst()
                        .orElse(null);
                
                if (existente != null) {
                    // Se já existe, apenas incrementa a quantidade
                    existente.setQuantidade(existente.getQuantidade() + 1);
                } else {
                    // Se não existe, cria um novo item no carrinho
                    // Obtém ou cria um pedido temporário (ainda não persistido no banco)
                    Pedido pedidoTemp = obterPedidoTemporario(session);
                    
                    // Cria novo item associando pedido, produto, quantidade e preço
                    PedidoProduto novoProduto = new PedidoProduto(
                        pedidoTemp,
                        produto,
                        1,  // Quantidade inicial
                        BigDecimal.valueOf(produto.getPreco())
                    );
                    // Adiciona o novo item ao carrinho
                    carrinho.add(novoProduto);
                }
                
                // Adiciona mensagem de sucesso na sessão
                session.setAttribute("mensagemSucesso", "Produto adicionado ao carrinho!");
            } else {
                // Produto não encontrado no banco
                model.addAttribute("erro", "Produto não encontrado.");
            }
        } catch (Exception e) {
            // Captura qualquer exceção durante o processo
            model.addAttribute("erro", "Erro ao adicionar produto: " + e.getMessage());
            e.printStackTrace();
        }

        // Atualiza o carrinho na sessão
        session.setAttribute("carrinho", carrinho);
        
        // Redireciona para a página anterior (referer) ou para home se não houver
        // Isso mantém o usuário navegando sem ser levado ao carrinho automaticamente
        return "redirect:" + (referer != null ? referer : "/");
    }

    // Mapeia requisições POST para "/carrinho/remover" - remove produto do carrinho
    @PostMapping("/carrinho/remover")
    public String removerDoCarrinho(
            // Captura o ID do produto a ser removido
            @RequestParam("produtoId") Long produtoId, 
            HttpSession session) {
        
        // Obtém o carrinho da sessão
        List<PedidoProduto> carrinho = obterCarrinho(session);
        
        // Remove todos os itens do carrinho que tenham o ID do produto especificado
        carrinho.removeIf(pp -> pp.getProduto().getId().equals(produtoId));
        
        // Atualiza o carrinho na sessão
        session.setAttribute("carrinho", carrinho);
        
        // Redireciona de volta para a página do carrinho
        return "redirect:/carrinho";
    }

    // Mapeia requisições POST para "/carrinho/atualizar" - atualiza quantidade de um item
    @PostMapping("/carrinho/atualizar")
    public String atualizarQuantidade(
            // Captura o ID do produto a ser atualizado
            @RequestParam("produtoId") Long produtoId,
            // Captura a nova quantidade desejada
            @RequestParam("quantidade") int quantidade,
            HttpSession session) {
        
        // Obtém o carrinho da sessão
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
        
        // Atualiza o carrinho na sessão
        session.setAttribute("carrinho", carrinho);
        
        // Redireciona de volta para a página do carrinho
        return "redirect:/carrinho";
    }

    // ------------------------------------------------------------------
    // CATÁLOGO COMPLETO
    // ------------------------------------------------------------------

    // Mapeia requisições GET para "/produtos/todos" - exibe todos os produtos por categoria
    @GetMapping("/produtos/todos")
    public String produtos(Model model){
        // Try-with-resources: abre conexão com banco de dados e fecha automaticamente
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            // Busca TODOS os produtos do banco
            List<Produto> todos = produtoDAO.listar();

            // Filtra e adiciona produtos masculinos ao modelo
            model.addAttribute("produtosMasculinos", filtrarPorSexo(todos, "masculino"));
            // Filtra e adiciona produtos femininos ao modelo
            model.addAttribute("produtosFemininos", filtrarPorSexo(todos, "feminino"));
            // Filtra e adiciona acessórios ao modelo
            model.addAttribute("produtosAcessorios", filtrarPorCategoria(todos, "acessorio"));
            
        } catch (Exception e) {
            // Captura qualquer exceção ao carregar produtos
            model.addAttribute("erro", "Erro ao carregar produtos: " + e.getMessage());
        }
        
        // Retorna o template que exibe todos os produtos organizados por categoria
        return "produtos";
    }
    
    // ------------------------------------------------------------------
    // CHECKOUT (FINALIZAÇÃO DE COMPRA)
    // ------------------------------------------------------------------

    // Mapeia requisições GET para "/checkout" - página de finalização de compra
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model){
        // Recupera o cliente logado da sessão
        Cliente logado = (Cliente) session.getAttribute("clienteLogado");
        
        // Verifica se o usuário está logado (obrigatório para finalizar compra)
        if (logado == null) {
            // Se não estiver logado, redireciona para home
            return "redirect:/";
        }

        // Obtém o carrinho da sessão
        List<PedidoProduto> carrinho = obterCarrinho(session);
        
        // Verifica se o carrinho está vazio
        if (carrinho.isEmpty()) {
            model.addAttribute("erro", "Carrinho vazio!");
            // Redireciona para o carrinho se estiver vazio
            return "redirect:/carrinho";
        }
        
        // Calcula o valor total da compra
        BigDecimal total = carrinho.stream()
                .map(pp -> pp.getPrecoUnitario().multiply(BigDecimal.valueOf(pp.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Adiciona dados ao modelo para exibição na página de checkout
        model.addAttribute("clienteLogado", logado);
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalCarrinho", total);
        model.addAttribute("totalCarrinhoFormatado", String.format("%.2f", total));
        
        // Retorna o template de checkout
        return "checkout";
    }

    // Mapeia requisições POST para "/processar-pedido" - finaliza a compra (versão temporária)
    @PostMapping("/processar-pedido")
    public String processarPedido(HttpSession session, Model model) {
        // Recupera o cliente logado da sessão
        Cliente logado = (Cliente) session.getAttribute("clienteLogado");
        
        // Verifica se o usuário está logado
        if (logado == null) {
            // Se não estiver logado, redireciona para home
            return "redirect:/";
        }
        
        // Obtém o carrinho da sessão
        List<PedidoProduto> carrinho = obterCarrinho(session);
        
        // Verifica se o carrinho está vazio
        if (carrinho.isEmpty()) {
            model.addAttribute("erro", "Carrinho vazio!");
            return "redirect:/carrinho";
        }
        
        // Limpa o carrinho para simular compra finalizada
        session.removeAttribute("carrinho");
        session.removeAttribute("pedidoTemporario");
        
        // Adiciona mensagem de sucesso na sessão para exibir na próxima página
        session.setAttribute("mensagemSucesso", "Pedido finalizado com sucesso!");
        
        // Redireciona para a página inicial
        return "redirect:/";
    }

    // ------------------------------------------------------------------
    // MÉTODOS AUXILIARES DE FILTRAGEM
    // ------------------------------------------------------------------

    /**
     * Filtra produtos pelo SEXO (case-insensitive)
     * Utilizado para separar produtos masculinos e femininos
     * 
     * @param produtos Lista completa de produtos
     * @param sexoAlvo Sexo desejado ("masculino" ou "feminino")
     * @return Lista filtrada de produtos
     */
    private List<Produto> filtrarPorSexo(List<Produto> produtos, String sexoAlvo) {
        // Validação: retorna lista vazia se parâmetros inválidos
        if (produtos == null || sexoAlvo == null) return List.of();

        // Converte para lowercase para comparação case-insensitive
        final String alvo = sexoAlvo.toLowerCase();

        // Stream API: filtra produtos cujo sexo contenha o valor alvo
        return produtos.stream()
                .filter(p -> {
                    String sexo = p.getSexo();
                    String categoria = p.getCategoria();
                    String sexoNorm = sexo != null ? sexo.toLowerCase() : "";
                    String catNorm = categoria != null ? categoria.toLowerCase() : "";

                    // Usa contains() para aceitar variações como "Masculino", "masc", "M", etc.
                    // Também considera quando o dado veio preenchido na coluna Categoria.
                    return sexoNorm.contains(alvo) || catNorm.contains(alvo);
                })
                .collect(Collectors.toList());
    }

    /**
     * Filtra produtos pela CATEGORIA (case-insensitive)
     * Utilizado para separar acessórios, roupas, calçados, etc
     * 
     * @param produtos Lista completa de produtos
     * @param categoriaAlvo Categoria desejada (ex: "acessorio")
     * @return Lista filtrada de produtos
     */
    private List<Produto> filtrarPorCategoria(List<Produto> produtos, String categoriaAlvo) {
        // Validação: retorna lista vazia se parâmetros inválidos
        if (produtos == null || categoriaAlvo == null) return List.of();

        // Converte para lowercase para comparação case-insensitive
        final String alvo = categoriaAlvo.toLowerCase();

        // Stream API: filtra produtos cuja categoria contenha o valor alvo
        return produtos.stream()
                .filter(p -> {
                    String cat = p.getCategoria();
                    if (cat == null) return false;
                    // Usa contains() para aceitar variações da categoria
                    return cat.toLowerCase().contains(alvo);
                })
                .collect(Collectors.toList());
    }

    /**
     * Carrega produtos de promoção, priorizando IDs específicos.
     * Se a lista estiver vazia, cai no filtro por categoria "promocao".
     */
    private void carregarProdutosPromocao(Model model) {
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);

            List<Produto> produtosPromo;
            if (IDS_PROMO_CONFIG.isEmpty()) {
                produtosPromo = filtrarPorCategoria(produtoDAO.listar(), "promocao");
            } else {
                produtosPromo = produtoDAO.listarPorIds(IDS_PROMO_CONFIG);
            }

            model.addAttribute("produtos", produtosPromo);
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar promoções: " + e.getMessage());
            model.addAttribute("produtos", List.of());
        }
    }

    // ------------------------------------------------------------------
    // MÉTODOS AUXILIARES DE CARREGAMENTO
    // ------------------------------------------------------------------

    /**
     * Carrega produtos filtrados por SEXO do banco de dados
     * Método reutilizável para as páginas masculino/feminino
     * 
     * @param model Model do Spring para passar dados à view
     * @param sexo Sexo a ser filtrado
     * @param atributo Nome do atributo no model (geralmente "produtos")
     */
    private void carregarProdutosPorSexo(Model model, String sexo, String atributo) {
        // Try-with-resources: abre conexão e fecha automaticamente
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            // Lista todos os produtos e filtra por sexo
            model.addAttribute(atributo, filtrarPorSexo(produtoDAO.listar(), sexo));
        } catch (Exception e) {
            // Em caso de erro, adiciona mensagem e lista vazia
            model.addAttribute("erro", "Erro ao carregar produtos: " + e.getMessage());
            model.addAttribute(atributo, List.of());
        }
    }

    /**
     * Carrega produtos filtrados por CATEGORIA do banco de dados
     * Método reutilizável para a página de acessórios
     * 
     * @param model Model do Spring para passar dados à view
     * @param categoria Categoria a ser filtrada
     * @param atributo Nome do atributo no model (geralmente "produtos")
     */
    private void carregarProdutosPorCategoria(Model model, String categoria, String atributo) {
        // Try-with-resources: abre conexão e fecha automaticamente
        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            // Lista todos os produtos e filtra por categoria
            model.addAttribute(atributo, filtrarPorCategoria(produtoDAO.listar(), categoria));
        } catch (Exception e) {
            // Em caso de erro, adiciona mensagem e lista vazia
            model.addAttribute("erro", "Erro ao carregar produtos: " + e.getMessage());
            model.addAttribute(atributo, List.of());
        }
    }

    // ------------------------------------------------------------------
    // MÉTODOS AUXILIARES DE GERENCIAMENTO DE CARRINHO
    // ------------------------------------------------------------------

    /**
     * Obtém o carrinho da sessão HTTP
     * Se não existir, cria um carrinho vazio
     * 
     * @param session Sessão HTTP do usuário
     * @return Lista de itens no carrinho (PedidoProduto)
     */
    private List<PedidoProduto> obterCarrinho(HttpSession session) {
        // Suprime warning do cast genérico
        @SuppressWarnings("unchecked")
        // Tenta obter o carrinho da sessão
        List<PedidoProduto> carrinho = (List<PedidoProduto>) session.getAttribute("carrinho");
        
        // Se não existir, cria um novo carrinho vazio
        if (carrinho == null) {
            carrinho = new ArrayList<>();
            session.setAttribute("carrinho", carrinho);
        }
        
        return carrinho;
    }

    /**
     * Cria ou obtém um pedido temporário para o carrinho
     * Este pedido só existe na sessão e NÃO é persistido no banco ainda
     * Será persistido apenas quando o usuário finalizar a compra no checkout
     * 
     * @param session Sessão HTTP do usuário
     * @return Pedido temporário com ID = -1 (flag de que não está no banco)
     */
    private Pedido obterPedidoTemporario(HttpSession session) {
        // Tenta obter o pedido temporário da sessão
        Pedido pedidoTemp = (Pedido) session.getAttribute("pedidoTemporario");
        
        // Se não existir, cria um novo pedido temporário
        if (pedidoTemp == null) {
            pedidoTemp = new Pedido();
            // ID = -1 indica que é temporário e ainda não foi salvo no banco
            pedidoTemp.setId(-1L);
            // Status CARRINHO diferencia de pedidos finalizados
            pedidoTemp.setStatus("CARRINHO");
            // Data de criação do carrinho
            pedidoTemp.setData(java.time.LocalDateTime.now());
            // Valor total será calculado no checkout
            pedidoTemp.setValorTotal(BigDecimal.ZERO);
            // Armazena na sessão para reutilização
            session.setAttribute("pedidoTemporario", pedidoTemp);
        }
        
        return pedidoTemp;
    }
}
