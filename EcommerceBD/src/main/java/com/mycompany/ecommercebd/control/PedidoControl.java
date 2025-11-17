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

@Controller
public class PedidoControl {

    // ✅ Página principal da área do cliente (Minha Conta)
    @GetMapping("/minha-conta")
    public String minhaConta(HttpSession session, Model model) {

        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        // Se não estiver logado, volta pra index com modal de login
        if (clienteLogado == null) {
            model.addAttribute("loginRequired", true);
            return "index";
        }

        try (Connection con = Conexao.conectar()) {
            ClienteDAO clienteDAO = new ClienteDAO(con);
            PedidoDAO pedidoDAO = new PedidoDAO(con);

            // Carrega dados atualizados do cliente
            Cliente clienteBD = clienteDAO.buscarPorId(clienteLogado.getId());
            List<Pedido> pedidos = pedidoDAO.listarPorCliente(clienteBD.getId());

            model.addAttribute("cliente", clienteBD);
            model.addAttribute("pedidos", pedidos);

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar sua conta: " + e.getMessage());
        }

        return "minha-conta";
    }

    // ✅ Atualizar dados do cliente (Meus dados)
    @PostMapping("/minha-conta/atualizar")
    public String atualizarDadosCliente(
            @RequestParam("nome") String nome,
            @RequestParam("cep") String cep,
            @RequestParam("cidade") String cidade,
            @RequestParam("logradouro") String logradouro,
            @RequestParam("numero") String numero,
            @RequestParam("bairro") String bairro,
            @RequestParam("estado") String estado,
            HttpSession session,
            Model model) {

        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        if (clienteLogado == null) {
            model.addAttribute("loginRequired", true);
            return "index";
        }

        try (Connection con = Conexao.conectar()) {
            ClienteDAO clienteDAO = new ClienteDAO(con);

            Cliente clienteBD = clienteDAO.buscarPorId(clienteLogado.getId());

            clienteBD.setNome(nome);
            clienteBD.setCep(cep);
            clienteBD.setCidade(cidade);
            clienteBD.setLogradouro(logradouro);
            clienteBD.setNumero(numero);
            clienteBD.setBairro(bairro);
            clienteBD.setEstado(estado);

            // 🔴 AQUI ERA atualizar(...) – AGORA É alterar(...)
            clienteDAO.alterar(clienteBD);

            // Atualiza sessão
            session.setAttribute("clienteLogado", clienteBD);
            model.addAttribute("cliente", clienteBD);

            // Recarrega pedidos para exibir na mesma tela
            PedidoDAO pedidoDAO = new PedidoDAO(con);
            List<Pedido> pedidos = pedidoDAO.listarPorCliente(clienteBD.getId());
            model.addAttribute("pedidos", pedidos);

            model.addAttribute("mensagemSucesso", "Dados atualizados com sucesso!");

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao atualizar seus dados: " + e.getMessage());
        }

        return "minha-conta";
    }

    // ✅ Detalhes de um pedido: produtos + nota fiscal
    @GetMapping("/minha-conta/pedidos/{id}")
    public String detalhesPedido(
            @PathVariable("id") Long idPedido,
            HttpSession session,
            Model model) {

        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        if (clienteLogado == null) {
            model.addAttribute("loginRequired", true);
            return "index";
        }

        try (Connection con = Conexao.conectar()) {
            PedidoDAO pedidoDAO = new PedidoDAO(con);
            PedidoProdutoDAO pedidoProdutoDAO = new PedidoProdutoDAO(con);
            NotaFiscalDAO notaFiscalDAO = new NotaFiscalDAO(con);

            Pedido pedido = pedidoDAO.buscarPorId(idPedido);

            // Garante que o pedido pertence ao cliente logado
            if (pedido == null || pedido.getId_cliente() == null
                    || !pedido.getId_cliente().getId().equals(clienteLogado.getId())) {
                model.addAttribute("erro", "Pedido não encontrado ou não pertence a você.");
                return "redirect:/minha-conta";
            }

            List<PedidoProduto> itens = pedidoProdutoDAO.listarPorPedido(idPedido);
            NotaFiscal notaFiscal = notaFiscalDAO.buscarPorPedido(idPedido);

            model.addAttribute("pedido", pedido);
            model.addAttribute("itens", itens);
            model.addAttribute("notaFiscal", notaFiscal);

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar detalhes do pedido: " + e.getMessage());
            return "redirect:/minha-conta";
        }

        return "pedido-detalhe";
    }
}
