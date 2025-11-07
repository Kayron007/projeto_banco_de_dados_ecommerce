/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author gustavo
 */
public class PedidoProduto extends EntidadeBase{
    private Pedido id_pedido;
    private Produto id_produtp;
    private int quantidade;
    private int precoUnitario;

    public PedidoProduto() {
        super();
    }

    public PedidoProduto(Long id, Pedido id_pedido, Produto id_produtp, int quantidade, int precoUnitario) {
        super(id);
        this.id_pedido = id_pedido;
        this.id_produtp = id_produtp;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    @Override
    protected String getTabela() {
        return "pedido_produto";
    }

    /*
     * Completar mais tarde
     */
    @Override
    public String toString() {
        return "";
    }

    /**
     * @return the id_pedido
     */
    public Pedido getId_pedido() {
        return id_pedido;
    }

    /**
     * @param id_pedido the id_pedido to set
     */
    public void setId_pedido(Pedido id_pedido) {
        this.id_pedido = id_pedido;
    }

    /**
     * @return the id_produtp
     */
    public Produto getId_produtp() {
        return id_produtp;
    }

    /**
     * @param id_produtp the id_produtp to set
     */
    public void setId_produtp(Produto id_produtp) {
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
