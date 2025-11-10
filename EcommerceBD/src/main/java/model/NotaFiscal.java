/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author gustavo
 */
public class NotaFiscal extends EntidadeBase{
    private LocalDateTime dataDeEmissao;
    private int valorTotal;
    private int imposto;
    private String chaveDeAcesso;
    private Pedido id_pedido;

    public NotaFiscal() {
        super();
    }

    public NotaFiscal(Long id, LocalDateTime dataDeEmissao, int valorTotal, int imposto, String chaveDeAcesso, Pedido id_pedido) {
        super(id);
        this.dataDeEmissao = dataDeEmissao;
        this.valorTotal = valorTotal;
        this.imposto = imposto;
        this.chaveDeAcesso = chaveDeAcesso;
        this.id_pedido = id_pedido;
    }

    /*
     * Completar mais tarde
     */
    @Override
    public String toString() {
        return "";
    }

    /**
     * @return the dataDeEmissao
     */
    public LocalDateTime getDataDeEmissao() {
        return dataDeEmissao;
    }

    /**
     * @param dataDeEmissao the dataDeEmissao to set
     */
    public void setDataDeEmissao(LocalDateTime dataDeEmissao) {
        this.dataDeEmissao = dataDeEmissao;
    }

    /**
     * @return the valorTotal
     */
    public int getValorTotal() {
        return valorTotal;
    }

    /**
     * @param valorTotal the valorTotal to set
     */
    public void setValorTotal(int valorTotal) {
        this.valorTotal = valorTotal;
    }

    /**
     * @return the imposto
     */
    public int getImposto() {
        return imposto;
    }

    /**
     * @param imposto the imposto to set
     */
    public void setImposto(int imposto) {
        this.imposto = imposto;
    }

    /**
     * @return the chaveDeAcesso
     */
    public String getChaveDeAcesso() {
        return chaveDeAcesso;
    }

    /**
     * @param chaveDeAcesso the chaveDeAcesso to set
     */
    public void setChaveDeAcesso(String chaveDeAcesso) {
        this.chaveDeAcesso = chaveDeAcesso;
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
    
    
}
