/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author gustavo
 */
public class pedido_produto extends entidadeBase{
    private pedido id_pedido;
    private produto id_produtp;
    private int quantidade;
    private int precoUnitario;

    public pedido_produto() {
    }

    public pedido_produto(pedido id_pedido, produto id_produtp, int quantidade, int precoUnitario, int id) {
        super(id);
        this.id_pedido = id_pedido;
        this.id_produtp = id_produtp;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    /**
     * @return the id_pedido
     */
    public pedido getId_pedido() {
        return id_pedido;
    }

    /**
     * @param id_pedido the id_pedido to set
     */
    public void setId_pedido(pedido id_pedido) {
        this.id_pedido = id_pedido;
    }

    /**
     * @return the id_produtp
     */
    public produto getId_produtp() {
        return id_produtp;
    }

    /**
     * @param id_produtp the id_produtp to set
     */
    public void setId_produtp(produto id_produtp) {
        this.id_produtp = id_produtp;
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

    /**
     * @return the precoUnitario
     */
    public int getPrecoUnitario() {
        return precoUnitario;
    }

    /**
     * @param precoUnitario the precoUnitario to set
     */
    public void setPrecoUnitario(int precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
    
    
}
