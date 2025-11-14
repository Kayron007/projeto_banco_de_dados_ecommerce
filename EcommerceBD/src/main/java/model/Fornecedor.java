package model;

public class Fornecedor extends EntidadeBase{  
    private String nome;
    private String tipoMaterial;
    private String cnpj;
    private String cep;
    private String email;
    
    /* Construtor padrão
     * Utilizado para criar um fornecedor vazio que será populado depois
     * O ID será gerado posteriormente pelo gerarIdUnico;
    */
    public Fornecedor() {
        super();
    }

    /*
     * Construtor completo para fornecedor sem cadastro
     * Utilizado para CADASTRAR um fornecedor
     * O ID será gerado automáticamente pelo método gerarIdUnico;
     */
    public Fornecedor(String nome, String tipoMaterial, String cnpj, String cep, String email) {
        super();
        this.nome = nome;
        this.tipoMaterial = tipoMaterial;
        this.cnpj = cnpj;
        this.cep = cep;
        this.email = email;
    }
    
    /*
     * Construtor completo para fornecedor já cadastrado;
     *  
     */
    public Fornecedor(Long id, String nome, String tipoMaterial, String cnpj, String cep, String email){
        super(id);
        this.nome = nome;
        this.tipoMaterial = tipoMaterial;
        this.cnpj = cnpj;
        this.cep = cep;
        this.email = email;
    }

    /* MÉTODOS DE VALIDAÇÃO */

    /**
     * Valida todos os dados do fornecedor
     * Lança exceções com mensagens específicas para cada erro;
     */
    public void validar() {
        validarNome(nome);
        validarTipoMaterial();
        validarCNPJ(cnpj);
        validarCEP(cep);
        validarEmail(email);
    }

    /*
     * Chama a classe Validador para validar o nome do fornecedor;
     */
    private void validarNome(String nome) {
        this.nome = Validador.nomeValido(nome);
    }

    /*
     * Valida o tipoMaterial
     */
    private void validarTipoMaterial() {
        if(tipoMaterial == null || tipoMaterial.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: Tipo de Material");
        }
    }

    /*
     * Chama a classe Validador para validar o CNPJ do fornecedor;
     */
    private void validarCNPJ(String cnpj) {
        this.cnpj = Validador.cnpjValido(cnpj);
    }

    /*
     * Chama a classe Validador para validar o CEP do fornecedor;
     */
    private void validarCEP(String cep) {
        this.cep = Validador.cepValido(cep);
    }

    /*
     * Chama a classe Validador para validar o email do fornecedor;
     */
    private void validarEmail(String email) {
        this.email = Validador.emailValido(email);
    }

    /* MÉTODO DE NORMALIZAÇÃO DE DADOS */

    /**
     * Normaliza os dados do fornecedor (formata, remove espaços, converte para letras maiúsculas/minúsculas)
     * Chamado antes de salvar no banco;
     */
    public void normalizar() {

        /*
         * Remove os espaços extras no nome;
         */
        if(nome !=  null) {
            nome = nome.trim();
        }

        /*
         * Remove os espaços extras no tipoMaterial;
         */
        if(tipoMaterial != null) {
            tipoMaterial = tipoMaterial.trim();
        }

        /*
         * Remove a máscara do CNPJ;
         */
        if(cnpj != null) {
            cnpj = cnpj.replaceAll("[^0-9]", "");
        }

        /*
         * Remove a máscara do CEP;
         */
        if(cep != null) {
            cep = cep.replaceAll("[^0-9]", "");
        }

        /*
         * Remove os espaços extras no email e converte a String para letras minúsculas;
         */
        if(email != null) {
            email = email.trim().toLowerCase();
        }
    }

    /* MÉTODOS UTILITÁRIOS */

    /*
     * Retorna o CEP formatado;
     */
    public String formataCEP() {
        return Validador.utilCEP(this.cep);
    }
    
    /*
     * Exibe as informações do fornecedor;
     */
    @Override
    public String toString() {
        return "Fornecedor{" +
        "ID: " + id +
        "Nome: " + nome +
        "Tipo de material: " + tipoMaterial +
        "CNPJ: " + cnpj +
        "CEP: " + cep +
        "Email: " + email + '\'' +
        "}";
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
     * @return the tipoMaterial
     */
    public String getTipoMaterial() {
        return tipoMaterial;
    }

    /**
     * @param tipoMaterial the tipoMaterial to set
     */
    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    /**
     * @return the cnpj
     */
    public String getCnpj() {
        return cnpj;
    }

    /**
     * @param cnpj the cnpj to set
     */
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
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
}