/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author gustavo
 */
public class cliente extends entidadeBase{
    private pessoa id_pessoa;
    private String nome;
    private String cep;
    private String logradouro;
    private String numero;
    private String bairo;
    private String estado;
    private String email;

    public cliente() {
    }

    public cliente(pessoa id_pessoa, String nome, String cep, String logradouro, String numero, String bairo, String estado, String email, int id) {
        super(id);
        this.id_pessoa = id_pessoa;
        this.nome = nome;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairo = bairo;
        this.estado = estado;
        this.email = email;
    }
    
    /**
     * @return the id_pessoa
     */
    public pessoa getId_pessoa() {
        return id_pessoa;
    }

    /**
     * @param id_pessoa the id_pessoa to set
     */
    public void setId_pessoa(pessoa id_pessoa) {
        this.id_pessoa = id_pessoa;
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
     * @return the bairo
     */
    public String getBairo() {
        return bairo;
    }

    /**
     * @param bairo the bairo to set
     */
    public void setBairo(String bairo) {
        this.bairo = bairo;
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
}


