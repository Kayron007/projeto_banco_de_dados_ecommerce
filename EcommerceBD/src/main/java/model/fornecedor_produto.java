/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author gustavo
 */
public class fornecedor_produto extends entidadeBase{
    private fornecedor id_fornecedor;
    private produto id_produto;
    private int precoFornecedor;
    private int prazo;

    public fornecedor_produto() {
    }

    public fornecedor_produto(fornecedor id_fornecedor, produto id_produto, int precoFornecedor, int prazo, int id) {
        super(id);
        this.id_fornecedor = id_fornecedor;
        this.id_produto = id_produto;
        this.precoFornecedor = precoFornecedor;
        this.prazo = prazo;
    }
    /**
     * @return the id_fornecedor
     */
    public fornecedor getId_fornecedor() {
        return id_fornecedor;
    }

    /**
     * @param id_fornecedor the id_fornecedor to set
     */
    public void setId_fornecedor(fornecedor id_fornecedor) {
        this.id_fornecedor = id_fornecedor;
    }

    /**
     * @return the id_produto
     */
    public produto getId_produto() {
        return id_produto;
    }

    /**
     * @param id_produto the id_produto to set
     */
    public void setId_produto(produto id_produto) {
        this.id_produto = id_produto;
    }

    /**
     * @return the precoFornecedor
     */
    public int getPrecoFornecedor() {
        return precoFornecedor;
    }

    /**
     * @param precoFornecedor the precoFornecedor to set
     */
    public void setPrecoFornecedor(int precoFornecedor) {
        this.precoFornecedor = precoFornecedor;
    }

    /**
     * @return the prazo
     */
    public int getPrazo() {
        return prazo;
    }

    /**
     * @param prazo the prazo to set
     */
    public void setPrazo(int prazo) {
        this.prazo = prazo;
    }
    
    
}
