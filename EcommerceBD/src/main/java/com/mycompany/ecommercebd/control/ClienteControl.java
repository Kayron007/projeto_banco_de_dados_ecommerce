
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommercebd.control;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycompany.ecommercebd.model.Cliente;
import com.mycompany.ecommercebd.model.Conexao;
import com.mycompany.ecommercebd.model.DAO.ClienteDAO;

import jakarta.servlet.http.HttpSession;


@Controller
public class ClienteControl {

    // ------------------------------------------------------------------
    // TELAS
    // ------------------------------------------------------------------

    @GetMapping("/cadastro")
    public String abrirCadastro() {
        return "cadastro";
    }

    @GetMapping("/login")
    public String abrirLogin() {
        return "login";
    }

    // ------------------------------------------------------------------
    // CADASTRO NORMAL
    // ------------------------------------------------------------------

    @PostMapping("/cadastro")
    public String cadastroCliente(
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
            Model model) {

        try (Connection con = Conexao.conectar()) {

            ClienteDAO dao = new ClienteDAO(con);

            // Verifica se o email já existe
            if (dao.buscarPorEmail(email) != null) {
                model.addAttribute("mensagem", "Email já cadastrado!");
                return "resultadoinvalido";
            }

            Cliente novo = new Cliente();
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

            dao.inserir(novo);

            model.addAttribute("email", email);
            return "resultado";

        } catch (Exception e) {
            model.addAttribute("mensagem", "Erro ao cadastrar: " + e.getMessage());
            return "resultadoinvalido";
        }
    }

    // ------------------------------------------------------------------
    // LOGIN
    // ------------------------------------------------------------------

    @PostMapping("/login")
    public String loginCliente(
            @RequestParam("email") String email,
            @RequestParam("senha") String senha,
            Model model,
            HttpSession session) {

        try (Connection con = Conexao.conectar()) {

            ClienteDAO dao = new ClienteDAO(con);
            Cliente c = dao.buscarPorEmail(email);

            if (c == null || c.getSenha() == null || !c.getSenha().equals(senha)) {
                model.addAttribute("erro", "Email ou senha inválidos!");
                return "login";
            }

            // 👉 Guarda o cliente na sessão
            session.setAttribute("clienteLogado", c);

            return "redirect:/";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao logar: " + e.getMessage());
            return "login";
        }
    }
}
/**
 *
 * @author gustavo
 */
@Controller
public class ClienteControl {

    private ClienteDAO clienteDAO;

    public ClienteControl() {
    }

    public void menu(Scanner scan) {
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n╔════════════════════════════════╗");
            System.out.println("║     MENU CLIENTE               ║");
            System.out.println("╚════════════════════════════════╝");
            System.out.println("1 - Login");
            System.out.println("2 - Cadastro");
            System.out.println("0 - Voltar");
            System.out.print("\nEscolha uma opção: ");

            int opt = scan.nextInt();
            scan.nextLine();

            switch (opt) {
                case 1:
                    loginCliente(scan);
                    break;
                case 2:
//                    cadastroCliente(scan);
                    break;
                case 0:
                    System.out.println("\nVoltando...");
                    continuar = false;
                    break;
                default:
                    System.out.println("\n✗ Opção inválida! Tente novamente.");
            }
        }
    }

    // ========================================================================
    // LOGIN
    // ========================================================================
    /**
     * Realiza o login do cliente no sistema.
     *
     * FLUXO: 1. Coleta email e senha 2. Obtém conexão com banco 3. Chama
     * DAO.buscarPorEmailESenha() 4. Se encontrar, exibe sucesso e redireciona
     * 5. Se não encontrar, exibe erro
     */
    public void loginCliente(Scanner scan) {
        System.out.println("\n╔════════════════════════════════╗");
        System.out.println("║         LOGIN                  ║");
        System.out.println("╚════════════════════════════════╝");

        // Coleta credenciais
        System.out.print("\nEmail: ");
        String email = scan.nextLine();

        System.out.print("Senha: ");
        String senha = scan.nextLine();

        // Validação básica
        if (email.trim().isEmpty() || senha.trim().isEmpty()) {
            System.out.println("\n✗ Email e senha são obrigatórios!");
            return;
        }

        Connection connection = null;

        try {
            // Obtém conexão
            connection = Conexao.conectar();

            // Cria DAO
            ClienteDAO dao = new ClienteDAO(connection);

            System.out.println("\n[INFO] Verificando credenciais...");

            // Busca cliente por email e senha
            Cliente cliente = dao.buscarPorEmail(email);

            if (cliente != null) {
                // Login bem-sucedido
                System.out.println("\n╔════════════════════════════════════════╗");
                System.out.println("║  ✓ LOGIN REALIZADO COM SUCESSO!       ║");
                System.out.println("╚════════════════════════════════════════╝");
                System.out.println("\nBem-vindo(a), " + cliente.getNome() + "!");
                System.out.println("ID: " + cliente.getId());
                System.out.println("Email: " + cliente.getEmail());

                // Aqui você pode redirecionar para o menu do cliente logado
                // menuClienteLogado(cliente, scan);
            } else {
                // Credenciais incorretas
                System.out.println("\n╔════════════════════════════════════════╗");
                System.out.println("║  ✗ EMAIL OU SENHA INCORRETOS          ║");
                System.out.println("╚════════════════════════════════════════╝");
                System.out.println("\nVerifique seus dados e tente novamente.");
            }

        } catch (SQLException e) {
            System.err.println("\n╔════════════════════════════════════════╗");
            System.err.println("║  ✗ ERRO AO REALIZAR LOGIN             ║");
            System.err.println("╚════════════════════════════════════════╝");
            System.err.println("\nErro: " + e.getMessage());
            System.err.println("\n[DEBUG] Detalhes técnicos:");
            e.printStackTrace();

        } catch (Exception e) {
            System.err.println("\n✗ Erro inesperado: " + e.getMessage());
            e.printStackTrace();

        } finally {
            // Fecha conexão
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
    }

    // ========================================================================
    // CADASTRO
    // ========================================================================
    /**
     * Realiza o cadastro de um novo cliente no sistema.
     *
     * FLUXO COMPLETO: 1. Coleta todos os dados do usuário 2. Valida senhas (se
     * conferem) 3. Valida dados básicos 4. Cria objeto Pessoa (simulado -
     * ajuste conforme seu sistema) 5. Cria objeto Cliente 6. Chama
     * DAO.inserir() que: - Normaliza os dados - Valida as regras de negócio -
     * Verifica email duplicado - Gera ID único automaticamente - Insere no
     * banco 7. Exibe sucesso com ID gerado
     */
    @PostMapping("/enviar")
    public String cadastroCliente(@RequestParam("nome") String nome,
            @RequestParam("tipo") String tipo, @RequestParam("email") String email,
            @RequestParam("senha") String senha, @RequestParam("cep") String cep,
            @RequestParam("cidade") String cidade, @RequestParam("logradouro") String logradouro,
            @RequestParam("numero") String numero, @RequestParam("bairro") String bairro,
            @RequestParam("uf") String uf) {
        Connection connection = null;
        System.out.println("Metodo cadastro foi chamado");

        try {
            connection = Conexao.conectar();

            // ================================================================
            // ETAPA 4: CRIAR OBJETO CLIENTE
            // ================================================================
            Cliente novoCliente = new Cliente(
                    tipo,
                    nome,
                    email,
                    senha,
                    cep,
                    cidade,
                    logradouro,
                    numero,
                    bairro,
                    uf
            );

            System.out.println("\n[DEBUG] Cliente criado na memória");
            System.out.println("[DEBUG] ID antes de inserir: " + novoCliente.getId()); // null

            // ================================================================
            // ETAPA 5: PERSISTIR NO BANCO (AQUI A MÁGICA ACONTECE!)
            // ================================================================
            ClienteDAO dao = new ClienteDAO(connection);

            System.out.println("\n[INFO] Processando cadastro...");

            // O método inserir() do DAO faz TUDO:
            // 1. cliente.normalizar() - formata os dados
            // 2. cliente.validar() - valida regras de negócio
            // 3. cliente.validarSenha() - valida senha
            // 4. Verifica se email já existe
            // 5. cliente.gerarIdUnico(connection) - gera ID único
            // 6. Executa INSERT no banco
            dao.inserir(novoCliente);

            // ================================================================
            // ETAPA 6: SUCESSO - EXIBIR FEEDBACK
            // ================================================================
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║  ✓ CADASTRO REALIZADO COM SUCESSO!    ║");
            System.out.println("╚════════════════════════════════════════╝");

            System.out.println("\n--- SEUS DADOS ---");
            System.out.println("ID gerado: " + novoCliente.getId());
            System.out.println("Nome: " + novoCliente.getNome());
            System.out.println("Email: " + novoCliente.getEmail());
            System.out.println("Endereço: " + novoCliente.getEnderecoCompleto());

            System.out.println("\n✓ Você já pode fazer login no sistema!");
            return "resultado";
        } catch (IllegalArgumentException e) {
            // Erros de validação (do método validar() do Cliente)
            System.err.println("\n╔════════════════════════════════════════╗");
            System.err.println("║  ✗ DADOS INVÁLIDOS                    ║");
            System.err.println("╚════════════════════════════════════════╝");
            System.err.println("\nErro: " + e.getMessage());
            return "resultadoinvalido";
        } catch (SQLException e) {
            // Erros do banco de dados
            System.err.println("\n╔════════════════════════════════════════╗");
            System.err.println("║  ✗ ERRO AO CADASTRAR CLIENTE          ║");
            System.err.println("╚════════════════════════════════════════╝");

            // Verifica códigos de erro específicos
            // Códigos variam por SGBD (MySQL, PostgreSQL, etc)
            if (e.getErrorCode() == 1062) {
                System.err.println("\nEmail já cadastrado no sistema!");
            } else {
                System.err.println("\nErro: " + e.getMessage());
            }

            /*System.err.println("\n[DEBUG] Detalhes técnicos:");
            e.printStackTrace();*/
            return "resultadoinvalido";

        } catch (IllegalStateException e) {
            // Erro na geração de ID único
            System.err.println("\n╔════════════════════════════════════════╗");
            System.err.println("║  ✗ ERRO AO GERAR ID ÚNICO             ║");
            System.err.println("╚════════════════════════════════════════╝");
            System.err.println("\nMotivo: " + e.getMessage());
            System.err.println("\nIsso pode indicar que:");
            System.err.println("- O banco está muito cheio");
            System.err.println("- Há problema de conexão");
            System.err.println("- O range de IDs é muito pequeno");
            return "resultadoinvalido";
        } catch (Exception e) {
            // Erros inesperados
            System.err.println("\n✗ Erro inesperado: " + e.getMessage());
            e.printStackTrace();
            return "resultadoinvalido";
        } finally {
            // ================================================================
            // ETAPA 7: LIMPEZA - SEMPRE EXECUTADA
            // ================================================================

            // Fecha a conexão SEMPRE, mesmo se houver erro
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("\n[DEBUG] Conexão fechada");
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
    }

    public boolean emailCadastrado(String email) {
        try {
            return clienteDAO.emailExistente(email);
        } catch (SQLException e) {
            System.out.println("Erro ao verificar email: " + e.getMessage());
            return false;
        }
    }
}

