/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import model.Cliente;
import model.Conexao;
import model.DAO.ClienteDAO;

/**
 *
 * @author gustavo
 */
public class clienteControl {
    private Scanner scan;
    
    public clienteControl(Scanner scan) {
        this.scan = scan;
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
            
            switch(opt) {
                case 1:
                    loginCliente(scan);
                    break;
                case 2:
                    cadastroCliente(scan);
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
     * FLUXO:
     * 1. Coleta email e senha
     * 2. Obtém conexão com banco
     * 3. Chama DAO.buscarPorEmailESenha()
     * 4. Se encontrar, exibe sucesso e redireciona
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
     * FLUXO COMPLETO:
     * 1. Coleta todos os dados do usuário
     * 2. Valida senhas (se conferem)
     * 3. Valida dados básicos
     * 4. Cria objeto Pessoa (simulado - ajuste conforme seu sistema)
     * 5. Cria objeto Cliente
     * 6. Chama DAO.inserir() que:
     *    - Normaliza os dados
     *    - Valida as regras de negócio
     *    - Verifica email duplicado
     *    - Gera ID único automaticamente
     *    - Insere no banco
     * 7. Exibe sucesso com ID gerado
     */
    public void cadastroCliente(Scanner scan) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    CADASTRO DE CLIENTE                 ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        // ====================================================================
        // ETAPA 1: COLETA DE DADOS
        // ====================================================================
        
        System.out.println("\n--- DADOS PESSOAIS ---");
        
        System.out.print("Nome completo: ");
        String nome = scan.nextLine();
        
        System.out.print("Email: ");
        String email = scan.nextLine();
        
        System.out.print("Tipo(Físico/Jurídico): ");
        String tipo = scan.nextLine();
        
        System.out.print("Senha: ");
        String senha = scan.nextLine();
        
        System.out.print("Confirme a senha: ");
        String senhaConfirmacao = scan.nextLine();
        
        System.out.println("\n--- ENDEREÇO ---");
        
        System.out.print("CEP: ");
        String cep = scan.nextLine();
        
        System.out.print("Logradouro (Rua/Avenida): ");
        String logradouro = scan.nextLine();
        
        System.out.print("Número: ");
        String numero = scan.nextLine();
        
        System.out.print("Bairro: ");
        String bairro = scan.nextLine();
        
        System.out.print("Estado (UF): ");
        String estado = scan.nextLine();
        
        // ====================================================================
        // ETAPA 2: VALIDAÇÕES BÁSICAS
        // ====================================================================
        
        // Valida se as senhas conferem
        if (!senha.equals(senhaConfirmacao)) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║  ✗ AS SENHAS NÃO CONFEREM!            ║");
            System.out.println("╚════════════════════════════════════════╝");
            return; // Interrompe o cadastro
        }
        
        // Valida campos obrigatórios
        if (nome.trim().isEmpty() || email.trim().isEmpty() || senha.trim().isEmpty()) {
            System.out.println("\n✗ Nome, email e senha são obrigatórios!");
            return;
        }
        
        if (cep.trim().isEmpty() || logradouro.trim().isEmpty() || 
            numero.trim().isEmpty() || bairro.trim().isEmpty() || estado.trim().isEmpty()) {
            System.out.println("\n✗ Todos os campos de endereço são obrigatórios!");
            return;
        }
        
        // ====================================================================
        // ETAPA 3: CRIAR OBJETO PESSOA
        // ====================================================================
        
        // IMPORTANTE: Aqui você precisa ajustar conforme sua implementação
        // de Pessoa. Este é apenas um exemplo simplificado.
        
        // Opção 1: Se Pessoa já existe no banco, busque pelo CPF ou crie
        // Opção 2: Se está criando nova Pessoa junto com Cliente
        
        Connection connection = null;
        
        try {
            connection = Conexao.conectar();
            
            // Ou se Pessoa tem outros dados obrigatórios:
            // pessoa.setNome(nome);
            // pessoa.setCpf(cpf); // você precisaria coletar o CPF
            // PessoaDAO pessoaDAO = new PessoaDAO(connection);
            // pessoaDAO.inserir(pessoa);
            
            // ================================================================
            // ETAPA 4: CRIAR OBJETO CLIENTE
            // ================================================================
            
            // IMPORTANTE: Em produção, criptografe a senha!
            // String senhaHash = BCrypt.hashpw(senha, BCrypt.gensalt());
            
            Cliente novoCliente = new Cliente(
                tipo,
                nome,
                cep,
                logradouro,
                numero,
                bairro,
                estado,
                email,
                senha  // Em produção: use senhaHash
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
            System.out.println("Pressione ENTER para continuar...");
            scan.nextLine();
            
        } catch (IllegalArgumentException e) {
            // Erros de validação (do método validar() do Cliente)
            System.err.println("\n╔════════════════════════════════════════╗");
            System.err.println("║  ✗ DADOS INVÁLIDOS                    ║");
            System.err.println("╚════════════════════════════════════════╝");
            System.err.println("\nErro: " + e.getMessage());
            
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
            
            System.err.println("\n[DEBUG] Detalhes técnicos:");
            e.printStackTrace();
            
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
            
        } catch (Exception e) {
            // Erros inesperados
            System.err.println("\n✗ Erro inesperado: " + e.getMessage());
            e.printStackTrace();
            
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
}