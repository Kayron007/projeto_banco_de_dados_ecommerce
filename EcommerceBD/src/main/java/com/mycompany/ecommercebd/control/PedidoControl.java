package com.mycompany.ecommercebd.control;

import java.sql.Connection;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycompany.ecommercebd.model.Cliente;
import com.mycompany.ecommercebd.model.Conexao;
import com.mycompany.ecommercebd.model.DAO.ClienteDAO;
import com.mycompany.ecommercebd.model.DAO.NotaFiscalDAO;
import com.mycompany.ecommercebd.model.DAO.PedidoDAO;
import com.mycompany.ecommercebd.model.DAO.PedidoProdutoDAO;
import com.mycompany.ecommercebd.model.NotaFiscal;
import com.mycompany.ecommercebd.model.Pedido;
import com.mycompany.ecommercebd.model.PedidoProduto;

import jakarta.servlet.http.HttpSession;

// Indica que esta classe é um Controller do Spring MVC, responsável por gerenciar requisições relacionadas a pedidos
@Controller
public class PedidoControl {

    // ------------------------------------------------------------------
    // ÁREA DO CLIENTE - MINHA CONTA
    // ------------------------------------------------------------------

    // Mapeia requisições GET para "/minha-conta" - página principal da área do cliente
    @GetMapping("/minha-conta")
    public String minhaConta(HttpSession session, Model model) {

        // Recupera o cliente logado da sessão
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        // Verifica se o usuário está logado
        if (clienteLogado == null) {
            // Se não estiver logado, indica que o modal de login deve ser aberto
            model.addAttribute("loginRequired", true);
            // Redireciona para a página inicial
            return "index";
        }

        // Try-with-resources: abre conexão com banco de dados e fecha automaticamente
        try (Connection con = Conexao.conectar()) {
            // Instancia os DAOs necessários
            ClienteDAO clienteDAO = new ClienteDAO(con);
            PedidoDAO pedidoDAO = new PedidoDAO(con);

            // Carrega dados atualizados do cliente diretamente do banco
            // Isso garante que as informações estejam sincronizadas
            Cliente clienteBD = clienteDAO.buscarPorId(clienteLogado.getId());
            
            // Lista todos os pedidos deste cliente
            List<Pedido> pedidos = pedidoDAO.listarPorCliente(clienteBD.getId());

            // Adiciona os dados ao modelo para exibição na view
            model.addAttribute("cliente", clienteBD);
            model.addAttribute("pedidos", pedidos);

        } catch (Exception e) {
            // Captura qualquer exceção durante o carregamento
            model.addAttribute("erro", "Erro ao carregar sua conta: " + e.getMessage());
        }

        // Retorna o template da área do cliente
        return "minha-conta";
    }

    // ------------------------------------------------------------------
    // ATUALIZAR DADOS DO CLIENTE
    // ------------------------------------------------------------------

    // Mapeia requisições POST para "/minha-conta/atualizar" - atualiza dados cadastrais do cliente
    @PostMapping("/minha-conta/atualizar")
    public String atualizarDadosCliente(
            // Captura todos os campos enviados pelo formulário de atualização
            @RequestParam("nome") String nome,
            @RequestParam("cep") String cep,
            @RequestParam("cidade") String cidade,
            @RequestParam("logradouro") String logradouro,
            @RequestParam("numero") String numero,
            @RequestParam("bairro") String bairro,
            @RequestParam("estado") String estado,
            // HttpSession para verificar o cliente logado
            HttpSession session,
            // Model para passar dados e mensagens para a view
            Model model) {

        // Recupera o cliente logado da sessão
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        // Verifica se o usuário está logado
        if (clienteLogado == null) {
            // Se não estiver logado, indica que o modal de login deve ser aberto
            model.addAttribute("loginRequired", true);
            return "index";
        }

        // Try-with-resources: abre conexão com banco de dados e fecha automaticamente
        try (Connection con = Conexao.conectar()) {
            ClienteDAO clienteDAO = new ClienteDAO(con);

            // Busca o cliente no banco para garantir que os dados estão atualizados
            Cliente clienteBD = clienteDAO.buscarPorId(clienteLogado.getId());

            // Atualiza os dados do cliente com as informações do formulário
            clienteBD.setNome(nome);
            clienteBD.setCep(cep);
            clienteBD.setCidade(cidade);
            clienteBD.setLogradouro(logradouro);
            clienteBD.setNumero(numero);
            clienteBD.setBairro(bairro);
            clienteBD.setEstado(estado);

            // Persiste as alterações no banco de dados
            // Usa alterar() ao invés de atualizar() conforme padrão do DAO
            clienteDAO.alterar(clienteBD);

            // Atualiza o cliente na sessão com os novos dados
            // Isso garante que as próximas páginas exibirão as informações atualizadas
            session.setAttribute("clienteLogado", clienteBD);
            model.addAttribute("cliente", clienteBD);

            // Recarrega a lista de pedidos para exibir na mesma tela
            PedidoDAO pedidoDAO = new PedidoDAO(con);
            List<Pedido> pedidos = pedidoDAO.listarPorCliente(clienteBD.getId());
            model.addAttribute("pedidos", pedidos);

            // Adiciona mensagem de sucesso para feedback ao usuário
            model.addAttribute("mensagemSucesso", "Dados atualizados com sucesso!");

        } catch (Exception e) {
            // Captura qualquer exceção durante a atualização
            model.addAttribute("erro", "Erro ao atualizar seus dados: " + e.getMessage());
        }

        // Retorna para a página "minha-conta" exibindo as alterações
        return "minha-conta";
    }

    // ------------------------------------------------------------------
    // DETALHES DE UM PEDIDO ESPECÍFICO
    // ------------------------------------------------------------------

    // Mapeia requisições GET para "/minha-conta/pedidos/{id}" - exibe detalhes de um pedido
    @GetMapping("/minha-conta/pedidos/{id}")
    public String detalhesPedido(
            // @PathVariable captura o ID do pedido da URL (ex: /minha-conta/pedidos/123 → id = 123)
            @PathVariable("id") Long idPedido,
            // HttpSession para verificar o cliente logado
            HttpSession session,
            // Model para passar dados para a view
            Model model) {

        // Recupera o cliente logado da sessão
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        
        // Verifica se o usuário está logado
        if (clienteLogado == null) {
            // Se não estiver logado, indica que o modal de login deve ser aberto
            model.addAttribute("loginRequired", true);
            return "index";
        }

        // Try-with-resources: abre conexão com banco de dados e fecha automaticamente
        try (Connection con = Conexao.conectar()) {
            // Instancia os DAOs necessários para buscar pedido, produtos e nota fiscal
            PedidoDAO pedidoDAO = new PedidoDAO(con);
            PedidoProdutoDAO pedidoProdutoDAO = new PedidoProdutoDAO(con);
            NotaFiscalDAO notaFiscalDAO = new NotaFiscalDAO(con);

            // Busca o pedido no banco pelo ID fornecido na URL
            Pedido pedido = pedidoDAO.buscarPorId(idPedido);

            // Validação de segurança: garante que o pedido pertence ao cliente logado
            // Isso evita que um usuário acesse pedidos de outros clientes alterando a URL
            if (pedido == null || pedido.getId_cliente() == null
                    || !pedido.getId_cliente().getId().equals(clienteLogado.getId())) {
                // Adiciona mensagem de erro
                model.addAttribute("erro", "Pedido não encontrado ou não pertence a você.");
                // Redireciona de volta para "minha-conta"
                return "redirect:/minha-conta";
            }

            // Busca todos os produtos (itens) deste pedido
            List<PedidoProduto> itens = pedidoProdutoDAO.listarPorPedido(idPedido);
            
            // Busca a nota fiscal associada a este pedido (pode ser null se ainda não foi gerada)
            NotaFiscal notaFiscal = notaFiscalDAO.buscarPorPedido(idPedido);

            // Adiciona os dados ao modelo para exibição na view
            model.addAttribute("pedido", pedido);
            model.addAttribute("itens", itens);
            model.addAttribute("notaFiscal", notaFiscal);

        } catch (Exception e) {
            // Captura qualquer exceção durante o carregamento dos detalhes
            model.addAttribute("erro", "Erro ao carregar detalhes do pedido: " + e.getMessage());
            // Redireciona para "minha-conta" em caso de erro
            return "redirect:/minha-conta";
        }

        // Retorna o template que exibe os detalhes completos do pedido
        return "pedido-detalhe";
    }
}