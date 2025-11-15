/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommercebd.model;

/**
 *
 * @author gustavo
 */
public class PessoaJuridica extends EntidadeBase{
    private Cliente id_cliente;
    private String cnpj;
    private String IE;

    /*
     * Construtor padrão
     * Utilizado para criar uma nova PessoaJuridica que será populada depois;
     */
    public PessoaJuridica() {
        super();
    }

    /*
     * Construtor completo para PessoaJuridica sem cadastro
     * Utilizado para cadastrar uma PessoaJuridica
     * O ID será gerado automáticamente pelo método gerarIdUnico;
     */
    public PessoaJuridica(Cliente id_cliente, String cnpj, String IE) {
        super();
        this.id_cliente = id_cliente;
        this.cnpj = cnpj;
        this.IE = IE;
    }

    /*
     * Construtor completo para PessoaJuridica já cadastrada;
     */
    public PessoaJuridica(Long id, Cliente id_cliente, String cnpj, String IE) {
        super(id);
        this.id_cliente = id_cliente;
        this.cnpj = cnpj;
        this.IE = IE;
    }

    /* MÉTODOS DE VALIDAÇÃO */

    /*
     * Valida todos os dados da PessoaJuridica
     * Lança exceções com mensagens específicas para cada erro;
     */
    public void validar() {
        validarIdCliente();
        validarCNPJ(cnpj);
        validarIE(IE);
    }

    /*
     * Verifica se o id do cliente é nulo
     * Retorna uma exceção em caso de erro;
     */
    private void validarIdCliente() {
        if(id_cliente == null) {
            throw new IllegalArgumentException("ERRO: Cliente não encontrado!");
        }
    }

    /*
     * Chama a classe Validador para validar o CNPJ;
     */
    private void validarCNPJ(String cnpj) {
        this.cnpj = Validador.cnpjValido(cnpj);
    }

    /*
     * Chama a classe Validador para validar o IE;
     */
    private void validarIE(String IE) {
        this.IE = Validador.utilIE(IE);
    }

    /* MÉTODO DE NORMALIZAÇÃO */

    /*
     * Normaliza os dados da PessoaJuridica
     * Chamado antes da validação
     */
    public void normalizar() {
        /*
         * Remove os espaços extras do CNPJ;
         */
        if(cnpj != null) {
            cnpj = cnpj.trim();
        }

        /*
         * Remove os espaços extras do IE;
         */
        if(IE != null) {
            IE = IE.trim();
        }
    }

    /*
     * Exibe os dados da PessoaJuridica;
     */
    @Override
    public String toString() {
        return "Pessoa Jurídica: {" +
        "\nNome: " + (id_cliente.getNome()) +
        "\nCNPJ: " + cnpj +
        "\nIE: " + IE +
        "\n }";
    }

    /**
     * @return the id_cliente
     */
    public Cliente getId_cliente() {
        return id_cliente;
    }

    /**
     * @param id_cliente the id_cliente to set
     */
    public void setId_cliente(Cliente id_cliente) {
        this.id_cliente = id_cliente;
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
     * @return the IE
     */
    public String getIE() {
        return IE;
    }

    /**
     * @param IE the IE to set
     */
    public void setIE(String IE) {
        this.IE = IE;
    }
    
    
}
