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

// Indica que esta classe é um Controller do Spring MVC, responsável por gerenciar requisições HTTP relacionadas a produtos
@Controller
public class ProdutoControl {

    // ------------------------------------------------------------------
    // DETALHES DO PRODUTO
    // ------------------------------------------------------------------

    // Mapeia requisições GET para "/produtos/{id}" e exibe os detalhes de um produto específico
    @GetMapping("/produtos/{id}")
    public String detalheProduto(
            // @PathVariable captura o ID da URL (ex: /produtos/5 → id = 5)
            @PathVariable("id") Long id, 
            // HttpSession gerencia dados da sessão do usuário
            HttpSession session, 
            // Model permite passar dados para a view
            Model model) {
        
        // Recupera o cliente logado da sessão (pode ser null se não estiver logado)
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        // Try-with-resources: abre conexão com banco de dados e fecha automaticamente ao final
        try (Connection con = Conexao.conectar()) {
            // Instancia os DAOs necessários
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO(con);

            // Busca o produto no banco pelo ID fornecido na URL
            Produto produto = produtoDAO.buscarPorId(id);
            
            // Verifica se o produto existe
            if (produto == null) {
                // Adiciona mensagem de erro ao modelo
                model.addAttribute("erro", "Produto nao encontrado.");
                // Redireciona para a página de listagem de produtos
                return "redirect:/produtos/todos";
            }

            // Busca todas as avaliações deste produto
            List<Avaliacao> avaliacoes = avaliacaoDAO.listarPorProduto(id);
            
            // Calcula a média das notas das avaliações
            // Se não houver avaliações, retorna 0
            double mediaAvaliacoes = avaliacoes.stream()
                    .mapToInt(av -> av.getNota())  // Extrai a nota de cada avaliação
                    .average()                      // Calcula a média
                    .orElse(0);                     // Se vazio, retorna 0

            // Adiciona os dados ao modelo para serem exibidos na view
            model.addAttribute("produto", produto);
            model.addAttribute("avaliacoes", avaliacoes);
            model.addAttribute("mediaAvaliacoes", mediaAvaliacoes);
            model.addAttribute("clienteLogado", clienteLogado);

            // Retorna o template HTML de detalhes do produto
            return "produto-detalhe";
            
        } catch (Exception e) {
            // Captura qualquer exceção durante o processo
            model.addAttribute("erro", "Erro ao carregar produto: " + e.getMessage());
            // Redireciona para a listagem de produtos em caso de erro
            return "redirect:/produtos/todos";
        }
    }

    // ------------------------------------------------------------------
    // SALVAR AVALIAÇÃO
    // ------------------------------------------------------------------

    // Mapeia requisições POST para "/produtos/{id}/avaliacoes" e salva uma nova avaliação
    @PostMapping("/produtos/{id}/avaliacoes")
    public String salvarAvaliacao(
            // Captura o ID do produto da URL
            @PathVariable("id") Long idProduto,
            // Captura a nota enviada pelo formulário (valor de 1 a 5)
            @RequestParam("nota") short nota,
            // Captura o comentário enviado pelo formulário
            @RequestParam("comentario") String comentario,
            // HttpSession para verificar se o usuário está logado
            HttpSession session,
            // Model para passar dados para a view
            Model model) {

        // Recupera o cliente logado da sessão
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        
        // Verifica se o usuário está logado
        if (clienteLogado == null) {
            // Salva a URL de redirecionamento para retornar após o login
            session.setAttribute("redirectAfterLogin", "/produtos/" + idProduto);
            // Indica que o modal de login deve ser aberto
            model.addAttribute("loginRequired", true);
            // Redireciona para a página inicial (index) onde o modal de login será exibido
            return "index";
        }

        // Try-with-resources: abre conexão com banco de dados e fecha automaticamente
        try (Connection con = Conexao.conectar()) {
            // Instancia os DAOs necessários
            ProdutoDAO produtoDAO = new ProdutoDAO(con);
            AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO(con);

            // Busca o produto no banco para validar se ele existe
            Produto produto = produtoDAO.buscarPorId(idProduto);
            
            // Verifica se o produto existe
            if (produto == null) {
                model.addAttribute("erro", "Produto nao encontrado.");
                return "redirect:/produtos/todos";
            }

            // Cria uma nova avaliação com os dados fornecidos
            Avaliacao avaliacao = new Avaliacao(nota, comentario, produto, clienteLogado);
            
            // Insere a avaliação no banco de dados
            avaliacaoDAO.inserir(avaliacao);

        } catch (Exception e) {
            // Captura qualquer exceção durante o salvamento da avaliação
            model.addAttribute("erro", "Erro ao salvar avaliacao: " + e.getMessage());
        }

        // Redireciona de volta para a página de detalhes do produto
        // Assim o usuário vê sua avaliação recém-adicionada
        return "redirect:/produtos/" + idProduto;
    }
}