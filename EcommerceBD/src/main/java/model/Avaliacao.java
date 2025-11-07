/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author gustavo
 */
public class Avaliacao extends EntidadeBase{
    private short nota;
    private String comentario;
    private Produto id_produto;
    private Cliente id_cliente;

    public Avaliacao() {
        super();
    }

    public Avaliacao(Long id, short nota, String comentario, Produto id_produto, Cliente id_cliente) {
        super(id);
        this.nota = nota;
        this.comentario = comentario;
        this.id_produto = id_produto;
        this.id_cliente = id_cliente;
    }

    @Override
    protected String getTabela() {
        return "avaliacao";
    }

    /*
     * Completar mais tarde
     */
    @Override
    public String toString() {
        return "";
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
}

