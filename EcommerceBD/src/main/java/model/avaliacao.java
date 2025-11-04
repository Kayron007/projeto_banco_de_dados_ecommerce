/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author gustavo
 */
public class avaliacao extends entidadeBase{
    private short nota;
    private String comentario;
    private produto id_produto;
    private cliente id_cliente;

    public avaliacao() {
    }

    public avaliacao(short nota, String comentario, produto id_produto, cliente id_cliente, int id) {
        super(id);
        this.nota = nota;
        this.comentario = comentario;
        this.id_produto = id_produto;
        this.id_cliente = id_cliente;
    }

    /**
     * @return the nota
     */
    public short getNota() {
        return nota;
    }

    /**
     * @param nota the nota to set
     */
    public void setNota(short nota) {
        this.nota = nota;
    }

    /**
     * @return the comentario
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * @param comentario the comentario to set
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
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
     * @return the id_cliente
     */
    public cliente getId_cliente() {
        return id_cliente;
    }

    /**
     * @param id_cliente the id_cliente to set
     */
    public void setId_cliente(cliente id_cliente) {
        this.id_cliente = id_cliente;
    }
}

