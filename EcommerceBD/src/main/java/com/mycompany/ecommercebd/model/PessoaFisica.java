/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommercebd.model;

/**
 *
 * @author gustavo
 */
public class PessoaFisica extends EntidadeBase{
    private Cliente id_cliente;
    private String cpf;
    private String rg;

    /*
     * Construtor padrão
     * Utilizado para criar uma PessoaFisica que será populada depois;
     */
    public PessoaFisica() {
        super();
    }

    /*
     * Construtor completo para PessoaFisica sem cadastro
     * Utilizado para cadastrar uma nova PessoaFisica
     * O ID será gerado automáticamente pelo método gerarIdUnico;
     */
    public PessoaFisica(Cliente id_cliente, String cpf, String rg) {
        super();
        this.id_cliente = id_cliente;
        this.cpf = cpf;
        this.rg = rg;
    }

    /*
     * Construtor completo para PessoaFisica já cadastrado;
     */
    public PessoaFisica(Long id, Cliente id_cliente, String cpf, String rg) {
        super(id);
        this.id_cliente = id_cliente;
        this.cpf = cpf;
        this.rg = rg;
    }

    /* MÉTODOS DE VALIDAÇÃO */

    /*
     * Valida todos os dados da PessoaFisica
     * Lança exceções com mensagens específicas para cada erro;
     */
    public void validar() {
        validarIdCliente();
        validarCpf(cpf);
        validarRG(rg);
    }

    /*
     * Verifica se o id_cliente é nulo;
     */
    private void validarIdCliente() {
        if(id_cliente == null) {
            throw new IllegalArgumentException("ERRO: Cliente não encontrado!");
        }
    }

    /*
     * Chama a classe Validador para validar o CPF da PessoaFisica;
     */
    private void validarCpf(String cpf) {
        this.cpf = Validador.utilCPF(cpf);
    }

    /*
     * Chama a classe Validador para validar o RG da PessoaFisica;
     */
    private void validarRG(String rg) {
        this.rg = Validador.utilRG(rg);
    }

    /* MÉTODO DE NORMALIZAÇÃO */

    /*
     * Normaliza os dados da PessoaFisica
     * Chamado antes da validação;
     */
    public void normalizar() {
        /*
         * Remove os espaços extras do CPF;
         */
        if(cpf != null)  {
            cpf = cpf.trim();
        }

        /*
         * Remove os espaços extras do RG;
         */
        if(rg != null) {
            rg = rg.trim();
        }
    }

    /*
     * Exibe os dados da PessoaFisica;
     */
    @Override
    public String toString() {
        return "Pessoa Física: {" +
        "\nNome: " + (id_cliente.getNome()) +
        "\nCPF: " + cpf +
        "\nRG: " + rg +
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
     * @return the cpf
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * @param cpf the cpf to set
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * @return the rg
     */
    public String getRg() {
        return rg;
    }

    /**
     * @param rg the rg to set
     */
    public void setRg(String rg) {
        this.rg = rg;
    }
}
