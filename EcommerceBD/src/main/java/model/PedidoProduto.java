/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

/**
 *
 * @author gustavo
 */
public class PedidoProduto extends EntidadeBase{
    private Pedido pedido;
    private Produto produto;
    private int quantidade;
    private BigDecimal precoUnitario;

    /*
     * Construtor padrão
     * Usado para quando os dados forem preenchidos posteriormente pelos setters;
     */
    public PedidoProduto() {}

    /*
     * Construtor completo
     * Utilizado para criar a relação com os dados já definidos
     */
    public PedidoProduto(Pedido pedido, Produto produto, int quantidade, BigDecimal precoUnitario) {
        setPedido(pedido);
        setProduto(produto);
        setQuantidade(quantidade);
        setPrecoUnitario(precoUnitario);
    }

    /*
     * Exibe os dados do PedidoProduto/Carrinho;
     */
    @Override
    public String toString() {
        return "Carrinho: {" +
        "\nPedido: " + (pedido != null ? pedido.getId() : "N/D") +
        "\nProduto: " + (produto != null ? produto.getNome() : "N/D") +
        "\nQuantidade: " + quantidade +
        "\nPreço unitário: " + precoUnitario +
        "\n}";
    }

    /**
     * @return the pedido
     */
    public Pedido getPedido() {
        return pedido;
    }

    /**
     * Define o pedido associado ao produto
     * Deve existir pedido válido (não pode ser nulo)
     * @param pedido the pedido to set;
     */
    public void setPedido(Pedido pedido) {
        if(pedido == null) {
            throw new IllegalArgumentException("ERRO: ID do pedido não foi encontrado!");
        }
        this.pedido = pedido;
    }

    /**
     * @return the produto
     */
    public Produto getProduto() {
        return produto;
    }

    /**
     * Define o produto associado ao pedido
     * Deve existir produto válido (não pode ser nulo)
     * @param produto the produto to set;
     */
    public void setProduto(Produto produto) {
        if(produto == null) {
            throw new IllegalArgumentException("ERRO: ID do produto não foi encontrado");
        }
        this.produto = produto;
    }

    /**
     * @return the quantidade
     */
    public int getQuantidade() {
        return quantidade;
    }

    /**
     * Define a quantidade de produtos em um só pedido
     * A quantidade deve ser maior que zero
     * @param quantidade the quantidade to set;
     */
    public void setQuantidade(int quantidade) {
        if(quantidade <= 0) {
            throw new IllegalArgumentException("ERRO: Quantidade de produtos inválida! Deve haver ao menos um produto no pedido.");
        }
        this.quantidade = quantidade;
    }

    /**
     * @return the precoUnitario
     */
    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    /**
     * Define o preço unitário dos produtos
     * Cada produto deve ter um preço unitário e deve ser maior ou igual a zero
     * @param precoUnitario the precoUnitario to set;
     */
    public void setPrecoUnitario(BigDecimal precoUnitario) {
        if(precoUnitario == null || precoUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("ERRO: O produto deve ter preço unitário!");
        }
        this.precoUnitario = precoUnitario;
    }
}