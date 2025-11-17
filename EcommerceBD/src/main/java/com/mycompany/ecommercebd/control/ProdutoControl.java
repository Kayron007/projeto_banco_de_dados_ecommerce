package com.mycompany.ecommercebd.control;

import java.sql.Connection;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycompany.ecommercebd.model.Avaliacao;
import com.mycompany.ecommercebd.model.Cliente;
import com.mycompany.ecommercebd.model.Conexao;
import com.mycompany.ecommercebd.model.DAO.AvaliacaoDAO;
import com.mycompany.ecommercebd.model.DAO.ProdutoDAO;
import com.mycompany.ecommercebd.model.Produto;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProdutoControl {

    @GetMapping("/produtos/{id}")
    public String detalheProduto(@PathVariable("id") Long id, HttpSession session, Model model) {
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO(con);

            Produto produto = produtoDAO.buscarPorId(id);
            if (produto == null) {
                model.addAttribute("erro", "Produto nao encontrado.");
                return "redirect:/produtos";
            }

            List<Avaliacao> avaliacoes = avaliacaoDAO.listarPorProduto(id);
            double mediaAvaliacoes = avaliacoes.stream()
                    .mapToInt(av -> av.getNota())
                    .average()
                    .orElse(0);

            model.addAttribute("produto", produto);
            model.addAttribute("avaliacoes", avaliacoes);
            model.addAttribute("mediaAvaliacoes", mediaAvaliacoes);
            model.addAttribute("clienteLogado", clienteLogado);

            return "produto-detalhe";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar produto: " + e.getMessage());
            return "redirect:/produtos";
        }
    }

    @PostMapping("/produtos/{id}/avaliacoes")
    public String salvarAvaliacao(
            @PathVariable("id") Long idProduto,
            @RequestParam("nota") short nota,
            @RequestParam("comentario") String comentario,
            HttpSession session,
            Model model) {

        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        if (clienteLogado == null) {
            session.setAttribute("redirectAfterLogin", "/produtos/" + idProduto);
            model.addAttribute("loginRequired", true);
            return "index";
        }

        try (Connection con = Conexao.conectar()) {
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO(con);

            Produto produto = produtoDAO.buscarPorId(idProduto);
            if (produto == null) {
                model.addAttribute("erro", "Produto nao encontrado.");
                return "redirect:/produtos";
            }

            Avaliacao avaliacao = new Avaliacao(nota, comentario, produto, clienteLogado);
            avaliacaoDAO.inserir(avaliacao);

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao salvar avaliacao: " + e.getMessage());
        }

        return "redirect:/produtos/" + idProduto;
    }
}
