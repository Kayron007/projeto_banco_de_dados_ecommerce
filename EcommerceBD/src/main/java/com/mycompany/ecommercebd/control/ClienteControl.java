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
import jakarta.servlet.http.HttpServletRequest;

// Indica que esta classe é um Controller do Spring MVC, responsável por gerenciar requisições HTTP
@Controller
public class ClienteControl {

    // ------------------------------------------------------------------
    // TELAS
    // ------------------------------------------------------------------

    // Mapeia requisições GET para "/cadastro" e retorna a view de cadastro
    @GetMapping("/cadastro")
    public String abrirCadastro() {
        // Retorna o nome do template HTML (cadastro.html) a ser renderizado
        return "cadastro";
    }

    // Mapeia requisições GET para "/login" e retorna a view de login
    @GetMapping("/login")
    public String abrirLogin() {
        // Retorna o nome do template HTML (login.html) a ser renderizado
        return "login";
    }

    // ------------------------------------------------------------------
    // CADASTRO NORMAL
    // ------------------------------------------------------------------

    // Mapeia requisições POST para "/cadastro" e processa o cadastro de um novo cliente
    @PostMapping("/cadastro")
    public String cadastroCliente(
            // Captura os parâmetros enviados pelo formulário HTML
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
            // Model permite passar dados para a view
            Model model, 
            // HttpSession gerencia dados durante a sessão do usuário
            HttpSession session) {

        // Try-with-resources: abre conexão com banco de dados e fecha automaticamente ao final
        try (Connection con = Conexao.conectar()) {

            // Instancia o DAO (Data Access Object) para operações com a tabela Cliente
            ClienteDAO dao = new ClienteDAO(con);

            // Verifica se já existe um cliente cadastrado com este email
            if (dao.buscarPorEmail(email) != null) {
                // Adiciona mensagem de erro ao modelo para exibir na view
                model.addAttribute("mensagem", "Email já cadastrado!");
                // Retorna para a página de erro
                return "resultadoinvalido";
            }

            // Cria um novo objeto Cliente
            Cliente novo = new Cliente();
            // Preenche os atributos do cliente com os dados do formulário
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

            // Insere o novo cliente no banco de dados
            dao.inserir(novo);

            // Adiciona o email ao modelo (pode ser usado na view de confirmação)
            model.addAttribute("email", email);
            // Armazena o cliente na sessão para mantê-lo logado
            session.setAttribute("clienteLogado", novo);
            // Redireciona para a página inicial após cadastro bem-sucedido
            return "redirect:/";

        } catch (Exception e) {
            // Captura qualquer exceção durante o processo de cadastro
            // Adiciona a mensagem de erro ao modelo
            model.addAttribute("mensagem", "Erro ao cadastrar: " + e.getMessage());
            // Retorna para a página de erro
            return "resultadoinvalido";
        }
    }

    // ------------------------------------------------------------------
    // LOGIN
    // ------------------------------------------------------------------

    // Mapeia requisições POST para "/login" e processa a autenticação do cliente
    @PostMapping("/login")
    public String loginCliente(
            // Captura email e senha enviados pelo formulário de login
            @RequestParam("email") String email,
            @RequestParam("senha") String senha,
            // Model para passar dados para a view
            Model model,
            // HttpSession para gerenciar dados da sessão do usuário
            HttpSession session,
            // HttpServletRequest para acessar informações da requisição HTTP
            HttpServletRequest request) {

        // Recupera o atributo de redirecionamento salvo na sessão (usado quando usuário tenta acessar carrinho sem login)
        String redirect = (String) session.getAttribute("redirectAfterLogin");
        // Obtém o nome do template da página de origem (ex: "acessorios", "masculino")
        String paginaOrigem = obterPaginaOrigem(request);

        // Try-with-resources: abre conexão com banco de dados e fecha automaticamente
        try (Connection con = Conexao.conectar()) {

            // Instancia o DAO para operações com a tabela Cliente
            ClienteDAO dao = new ClienteDAO(con);
            // Busca o cliente no banco de dados pelo email fornecido
            Cliente c = dao.buscarPorEmail(email);

            // Verifica se o cliente não existe, ou se a senha está nula, ou se a senha está incorreta
            if (c == null || c.getSenha() == null || !c.getSenha().equals(senha)) {
                // Adiciona mensagem de erro ao modelo
                model.addAttribute("erro", "Email ou senha inválidos!");
                // Indica que o modal de login deve ser aberto novamente
                model.addAttribute("loginRequired", true);
                // Retorna para a página de origem mantendo o modal aberto
                return paginaOrigem;
            }

            // Login bem-sucedido: armazena o cliente na sessão
            session.setAttribute("clienteLogado", c);

            // Remove o atributo de redirecionamento da sessão para evitar comportamentos indesejados em logins futuros
            session.removeAttribute("redirectAfterLogin");

            // Se existe um redirecionamento específico salvo (ex: quando tentou acessar carrinho)
            if (redirect != null && !redirect.isEmpty()) {
                // Redireciona para a URL específica (ex: "/carrinho")
                return "redirect:" + redirect;
            }

            // Caso contrário, redireciona de volta para a página de origem
            return "redirect:" + obterPathOrigem(request);

        } catch (Exception e) {
            // Captura qualquer exceção durante o processo de login
            // Adiciona a mensagem de erro ao modelo
            model.addAttribute("erro", "Erro ao logar: " + e.getMessage());
            // Indica que o modal de login deve ser aberto novamente
            model.addAttribute("loginRequired", true);
            // Retorna para a página de origem mantendo o modal aberto
            return paginaOrigem;
        }
    }

    // ------------------------------------------------------------------
    // LOGOUT
    // ------------------------------------------------------------------

    // Mapeia requisições GET para "/logout" e encerra a sessão do usuário
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalida toda a sessão, removendo todos os atributos (incluindo clienteLogado)
        session.invalidate();
        // Redireciona para a página inicial
        return "redirect:/";
    }

    // ------------------------------------------------------------------
    // MÉTODOS AUXILIARES
    // ------------------------------------------------------------------

    // Método auxiliar: retorna o nome do template da página de origem
    // Usado quando há erro no login para manter o usuário na mesma página
    private String obterPaginaOrigem(HttpServletRequest request) {
        // Obtém o cabeçalho HTTP "Referer" que contém a URL da página anterior
        String referer = request.getHeader("Referer");
        // Se o referer é nulo ou vazio, retorna "index" como padrão
        if (referer == null || referer.isEmpty()) {
            return "index";
        }
        
        try {
            // Converte a URL do referer em um objeto URI para extrair o path
            String path = new java.net.URI(referer).getPath();
            // Se o path é nulo ou contém "/login", retorna "index" para evitar loops
            if (path == null || path.contains("/login")) {
                return "index";
            }
            // Mapeia o path para o nome do template correspondente
            return mapearPathParaTemplate(path);
        } catch (Exception e) {
            // Em caso de erro ao processar a URI, retorna "index" como fallback
            return "index";
        }
    }

    // Método auxiliar: retorna o path da URL de origem
    // Usado após login bem-sucedido para redirecionar o usuário de volta à página onde estava
    private String obterPathOrigem(HttpServletRequest request) {
        // Obtém o cabeçalho HTTP "Referer" que contém a URL da página anterior
        String referer = request.getHeader("Referer");
        // Se o referer é nulo ou vazio, retorna "/" (index) como padrão
        if (referer == null || referer.isEmpty()) {
            return "/";
        }
        
        try {
            // Converte a URL do referer em um objeto URI para extrair o path
            String path = new java.net.URI(referer).getPath();
            // Retorna o path se for válido e não contiver "/login", caso contrário retorna "/"
            return (path != null && !path.contains("/login")) ? path : "/";
        } catch (Exception e) {
            // Em caso de erro ao processar a URI, retorna "/" como fallback
            return "/";
        }
    }

    // Método auxiliar: mapeia path → nome do template
    // Converte o path da URL (ex: "/acessorios") para o nome do template Thymeleaf (ex: "acessorios")
    private String mapearPathParaTemplate(String path) {
        // Verifica se o path contém cada categoria e retorna o template correspondente
        if (path.contains("/masculino")) return "masculino";
        if (path.contains("/feminino")) return "feminino";
        if (path.contains("/acessorios")) return "acessorios";
        if (path.contains("/promocoes")) return "promocoes";
        if (path.contains("/carrinho")) return "carrinho";
        if (path.contains("/produtos")) return "produtos";
        // Se não encontrar correspondência, retorna "index" como padrão
        return "index";
    }
}