/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author gustavo
 */
public class Cliente extends EntidadeBase{
    private String tipo;
    private String nome;
    private String cep;
    private String logradouro;
    private String numero;
    private String bairro;
    private String estado;
    private String email;
    private String senha;

    /*Construtor padrão
    * Utilizado para criar um cliente vazio que será populado depois
    * O ID será gerado posteriormente via gerarIdUnico;
    */
    public Cliente() {
        super();
    }

    /**
     *Construtor completo para cliente sem cadastro
     * Utilizado para CADASTRAR um novo cliente
     * O ID será gerado automáticamente pelo método gerarIdUnico;
     */
    public Cliente(String tipo, String nome, String email, String senha, String cep, String logradouro, 
    String numero, String bairro, String estado) {
        super();
        this.tipo = tipo;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.estado = estado;
    }

    /*
     * Construtor completo para cliente já cadastrado;
     */
    public Cliente(Long id, String tipo, String nome, String email, String senha, String cep, String logradouro, 
    String numero, String bairro, String estado) {
        super(id);
        this.tipo = tipo;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.estado = estado;
    }

    /*
     * Acessa a tabela no banco de dados através do nome exato;
     */
    @Override
    protected String getTabela() {
        return "cliente";
    }

    /* MÉTODOS DE VALIDAÇÃO */

    /**
     * Valida todos os dados do cliente
     * Lança exceções com mensagens específicas para cada erro;
     */
    public void validar() {
        validarNome();
        validarEmail();
        validarSenha();
        validarCEP();
        validarEstado();
        validarEndereco();
    }

    /**
     * Valida o nome do cliente;
     */
    private void validarNome() {
        if(nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório!");
        }

        if(nome.trim().length() < 3) {
            throw new IllegalArgumentException("O nome deve ter um mínimo de três caracteres!");
        }
    }

    /**
     * Valida o email do cliente;
     */
    private void validarEmail() {
        if(email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: Email");
        }

        //String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        //if(!email.matches(regex)) {
        //   throw new IllegalArgumentException("Email inválido!");
        //}
    }

    /**
     * Valida a senha do usuário;
     */
    public void validarSenha() {
        if(senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: Senha");
        }

//        if(senha.trim().length() < 6) {
//            throw new IllegalArgumentException("A senha deve conter um mínimo de seis caracteres!");
//        }
    }

    /**
     * Valida o CEP do cliente;
     */
    private void validarCEP() {
        if(cep == null || cep.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: CEP");
        }

        String cepLimpo = cep.replaceAll("[^0-9]", "");
//        if(cepLimpo.length() != 7) {
//            throw new IllegalArgumentException("O CEP deve conter apenas oito caracteres!");
//        }
   }

    /**
     * Valida o estado (UF);
     */
    private void validarEstado() {
        if(estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: Estado");
        }

//        if(estado.trim().length() != 2) {
//            throw new IllegalArgumentException("Estado deve ter dois caracteres (UF)");
//        }
    }

    /**
     * Valida os campos de endereço;
     */
    private void validarEndereco() {
        if(logradouro == null || logradouro.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: Logradouro");
        }

        if(numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: Número");
        }

        if(bairro == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: Bairro");
        }
    }

    /* MÉTODOS DE NORMALIZAÇÃO DE DADOS */

    /**
     * Normaliza os dados dos clientes (formata, remove espaços, etc)
     * Chamado antes de salvar no banco;
     */
    public void normalizar() {
        
        /*
         * Remove os espaços extras no nome;
         */
        if(nome != null) {
            nome = nome.trim();
        }

        /*
         * Remove os espaços no email e converte a String para letras minúsculas;
         */
        if(email != null) {
            email = email.trim().toLowerCase();
        }

        /*
         * Remove qualquer caractere do CEP que não seja número (mantém apenas dígitos);
         */
        if(cep!= null) {
            cep = cep.replaceAll("[^0-9]", "");
        }

        /*
         * Remove os espaços extras no logradouro;
         */
        if(logradouro != null) {
            logradouro = logradouro.trim();
        }

        /*
         * Remove os espaços no número (complemento) e converte a String para letras maiúsculas;
         */
        if(numero != null) {
            numero = numero.trim().toUpperCase();
        }

        /*
         * Remove os espaços extras no bairro;
         */
        if(bairro != null) {
            bairro = bairro.trim();
        }

        /*
         * Remove os espaços no estado e converte a String para letras maiúsculas;
         */
        if(estado != null) {
            estado = estado.trim().toUpperCase();
        }
        
        if(tipo != null) {
            setTipo(getTipo().trim().toUpperCase());
        }
    }

    /* MÉTODOS UTILITÁRIOS */

    public String formatarCEP() {
        if(cep == null || cep.length() != 8) {
            return cep;
        }
        return cep.substring(0, 5) + "-" + cep.substring(5);
    }

    /*
     * Retorna o endereço completo formatado
     */
    public String getEnderecoCompleto() {
        return String.format("%s, %s - %s, %s - CEP: %s", logradouro, numero, bairro, estado, formatarCEP());
    }

    /*
     * Exibe as informações do cliente;
     */
    @Override
    public String toString() {
        return 
        "Cliente{" + 
        "ID: " + id +
        "\nidPessoa: " + (getTipo() != null ? getTipo() : "null") +
        "\nNome: " + nome +
        "\nEmail: " + email +
        "\nEndereço: " + getEnderecoCompleto() + '\'' +
        '}';
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the cep
     */
    public String getCep() {
        return cep;
    }

    /**
     * @param cep the cep to set
     */
    public void setCep(String cep) {
        this.cep = cep;
    }

    /**
     * @return the logradouro
     */
    public String getLogradouro() {
        return logradouro;
    }

    /**
     * @param logradouro the logradouro to set
     */
    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    /**
     * @return the numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * @return the bairro
     */
    public String getBairro() {
        return bairro;
    }

    /**
     * @param bairro the bairo to set
     */
    public void setBairo(String bairro) {
        this.bairro = bairro;
    }

    /**
     * @return the estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the senha
     */

    public String getSenha() {
        return senha;
    }

    /**
     * @param senha the senha to set
     */

    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}