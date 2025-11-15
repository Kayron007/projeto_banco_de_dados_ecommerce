/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommercebd.model;
/**
 *
 * @author gustavo
 */
public class Produto extends EntidadeBase{
    private String nome;
    private String descricao;
    private String tamanho;
    private String categoria;
    private String precoStr;
    private Double preco;
    private String sexo;
    private int quantidade;

    /*
     * Construtor padrão
     * Utilizado para criar um Produto que será populado depois;
     */
    public Produto() {
        super();
    }

    /*
     * Construtor completo para Produto sem cadastro
     * Utilizado para CADASTRAR um novo produto
     * O ID será gerado automáticamente posteriormente pelo método gerarIdUnico;
     */
    public Produto(String nome, String descricao, String tamanho, String categoria, String preco, int quantidade, String sexo) {
        super();
        this.nome = nome;
        this.descricao = descricao;
        this.tamanho = tamanho;
        this.categoria = categoria;
        setPreco(preco);
        this.quantidade = quantidade;
        this.sexo = sexo;
    }

    /*
     * Construtor completo para Produto já cadastrado;
     */
    public Produto(Long id, String nome, String descricao, String tamanho, String categoria, Double preco, int quantidade, String sexo) {
        super(id);
        this.nome = nome;
        this.descricao = descricao;
        this.tamanho = tamanho;
        this.categoria = categoria;
        this.preco = preco;
        this.quantidade = quantidade;
        this.sexo = sexo;
    }

    /* MÉTODOS DE VALIDAÇÃO */

    /*
     * Valida todos os dados do produto
     * Lança exceções com mensagens específicas para cada erro
     */
    public void validar() {
        validarNome(nome);
        validarDesc();
        validarTamanho();
        validarCategoria();
        validarPreco();
        validarQuantidade();
    }

    /*
     * Chama a classe Validador para validar o nome do Produto;
     */
    private void validarNome(String nome) {
        this.nome = Validador.nomeValido(nome);
    }

    /*
     * Valida a descrição do produto;
     */
    private void validarDesc() {
        if(descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: Descrição");
        }
    }

    /*
     * Valida o tamanho do produto;
     */
    private void validarTamanho() {
        if(tamanho == null || tamanho.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: Tamanho");
        }
    }

    /*
     * Valida a categoria do produto;
     */
    private void validarCategoria() {
        if(categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: Categoria");
        }
    }

    /*
     * Valida o preço do produto;
     */
    private void validarPreco() {
        if(preco <= 0) {
            throw new IllegalArgumentException("O preço do produto deve ser maior que zero!");
        }
    }

    /*
     * Valida a quantidade do produto;
     */
    private void validarQuantidade() {
        if(quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero!");
        }
    }

    /* MÉTODO DE NORMALIZAÇÃO */

    /**
     * Normaliza os dados dos clientes (formata, remove espaços e converte para letras maiúsculas/minúsculas)
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
         * Remove os espaços extras na descrição;
         */
        if(descricao != null) {
            descricao = descricao.trim();
        }

        /*
         * Remove os espaços extras no tamanho e converte a String pra letras minúsculas;
         */
        if(tamanho != null) {
            tamanho = tamanho.trim().toLowerCase();
        }

        /*
         * Remove os espaços extras na categoria;
         */
        if(categoria != null) {
            categoria = categoria.trim().toLowerCase();
        }
    }

    /*
     * Exibe os dados do Produto
     */
    @Override
    public String toString() {
        return "Produto{" +
        "\nNome: " + nome +
        "\nDescrição: " + descricao +
        "\nTamanho: " + tamanho +
        "\nCategoria: " + categoria +
        "\nPreço: " + preco +
        "\nQuantidade: " + quantidade +
        "\nSexo: " + sexo +
        "}";
    }

    public String getPrecoStr() {
        return precoStr;
    }

    public void setPrecoStr(String precoStr) {
        this.precoStr = precoStr;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
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
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao the descricao to set
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * @return the tamanho
     */
    public String getTamanho() {
        return tamanho;
    }

    /**
     * @param tamanho the tamanho to set
     */
    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    /**
     * @return the categoria
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * @param categoria the categoria to set
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * @return the preco
     */
    public Double getPreco() {
        return preco;
    }

    /**
     * Setter usado quando o valor vem da UI como String;
     */
    public void setPreco(String preco) {
        this.preco = Double.parseDouble(precoStr.trim());
    }

    /**
     * @param preco the preco to set;
     */
    public void setPreco(double preco) {
        this.preco = preco;
    }

    /**
     * @return the quantidade
     */
    public int getQuantidade() {
        return quantidade;
    }

    /**
     * @param quantidade the quantidade to set
     */
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}