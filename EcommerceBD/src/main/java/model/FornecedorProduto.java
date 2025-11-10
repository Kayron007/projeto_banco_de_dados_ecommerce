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
public class FornecedorProduto extends EntidadeBase{
    private Fornecedor id_fornecedor;
    private Produto id_produto;
    private BigDecimal precoFornecedor;
    private Integer prazo;

    /**
     * Construtor padrão
     * Usado quando o objeto for preenchido posteriormente pelos setters;
     */
    public FornecedorProduto() {}

    /**
     * Construtor completo
     * Utilizado para criar a relação com os dados já definidos;
     */
    public FornecedorProduto(Fornecedor id_fornecedor, Produto id_produto, BigDecimal precoFornecedor, int prazo) {
        setIdFornecedor(id_fornecedor);
        setIdProduto(id_produto);
        setPrecoFornecedor(precoFornecedor);
        setPrazo(prazo);
    }
    
    protected String getTabela(){
        return "fornecedor_produto";
    }

    /*
     * Exibe os dados da entidade associativa FornecedorProduto
     */
    @Override
    public String toString() {
        return "FornecedorProduto{" +
        "\nFornecedor: " + id_fornecedor.getNome() +
        "\nProduto: " + id_produto.getNome() +
        "\nPreço do Fornecedor: R$" + precoFornecedor +
        "\nPrazo: " + prazo + "dias" +
        "}";
    }

    /**
     * @return the id_fornecedor
     */
    public Fornecedor getIdFornecedor() {
        return id_fornecedor;
    }

    /**
     * Define o fornecedor associado ao produto
     * Deve existir fornecedor válido (não pode ser nulo)
     * @param id_fornecedor the id_fornecedor to set;
     */
    public void setIdFornecedor(Fornecedor id_fornecedor) {
        if(id_fornecedor == null) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: Fornecedor");
        }
        this.id_fornecedor = id_fornecedor;
    }

    /**
     * @return the id_produto
     */
    public Produto getIdProduto() {
        return id_produto;
    }

    /**
     * Define o produto associado ao fornecedor
     * Deve existir produto válido (não pode ser nulo)
     * @param id_produto the id_produto to set;
     */
    public void setIdProduto(Produto id_produto) {
        if(id_produto == null) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: Produto");
        }
        this.id_produto = id_produto;
    }

    /**
     * @return the precoFornecedor
     */
    public BigDecimal getPrecoFornecedor() {
        return precoFornecedor;
    }

    /**
     * Define o preço negocioado com o fornecedor
     * O preço não pode ser nulo e deve ser maior que zero
     * @param precoFornecedor the precoFornecedor to set;
     */
    public void setPrecoFornecedor(BigDecimal precoFornecedor) {
        if(precoFornecedor == null || precoFornecedor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço do fornecedor deve ser maior que zero.");
        }
        this.precoFornecedor = precoFornecedor;
    }

    /**
     * @return the prazo
     */
    public Integer getPrazo() {
        return prazo;
    }

    /**
     * Define o prazo de entrega para o fornecedor desse produto
     * O prazo não pode ser nulo e deve ser maior que um (dia);
     * @param prazo the prazo to set
     */
    public void setPrazo(Integer prazo) {
        if(prazo == null || prazo < 1) {
            throw new IllegalArgumentException("O prazo deve ser no mínimo 1 dia.");
        }
        this.prazo = prazo;
    }
}
