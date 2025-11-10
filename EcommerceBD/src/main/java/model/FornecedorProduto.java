/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author gustavo
 */
public class FornecedorProduto extends EntidadeBase{
    private Fornecedor id_fornecedor;
    private Produto id_produto;
    private int precoFornecedor;
    private int prazo;

    public FornecedorProduto() {
        super();
    }

    public FornecedorProduto(Fornecedor id_fornecedor, Produto id_produto, int precoFornecedor, int prazo) {
        this.id_fornecedor = id_fornecedor;
        this.id_produto = id_produto;
        this.precoFornecedor = precoFornecedor;
        this.prazo = prazo;
    }
    
    protected String getTabela(){
        return "fornecedor_produto";
    }

    /*
     * Completar mais tarde
     */
    @Override
    public String toString() {
        return "";
    }

    /**
     * @return the id_fornecedor
     */
    public Fornecedor getId_fornecedor() {
        return id_fornecedor;
    }

    /**
     * @param id_fornecedor the id_fornecedor to set
     */
    public void setId_fornecedor(Fornecedor id_fornecedor) {
        this.id_fornecedor = id_fornecedor;
    }

    /**
     * @return the id_produto
     */
    public Produto getId_produto() {
        return id_produto;
    }

    /**
     * @param id_produto the id_produto to set
     */
    public void setId_produto(Produto id_produto) {
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
