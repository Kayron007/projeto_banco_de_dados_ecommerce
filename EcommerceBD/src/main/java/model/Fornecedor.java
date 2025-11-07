package model;

public class Fornecedor extends EntidadeBase{  
    private String nome;
    private String tipoMaterial;
    private String cnpj;
    private String cep;
    private String email;
    
    //construtor vazio 
    public Fornecedor() {
        super();
    }
    
    public Fornecedor(Long id, String nome, String tipoMaterial, String cnpj, String cep, String email){
        super(id);
        this.nome = nome;
        this.tipoMaterial = tipoMaterial;
        this.cnpj = cnpj;
        this.cep = cep;
        this.email = email;
    }

    @Override
    protected String getTabela() {
        return "fornecedor";
    }

    /*
     * Completar mais tarde
     */
    @Override
    public String toString() {
        return "";
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
