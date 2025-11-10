/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author gustavo
 */
public class PessoaJuridica extends EntidadeBase{
    private Cliente id_cliente;
    private String cnpj;
    private String IE;

    public PessoaJuridica() {
        super();
    }

    public PessoaJuridica(Long id, Cliente id_cliente, String cnpj, String IE) {
        super(id);
        this.id_cliente = id_cliente;
        this.cnpj = cnpj;
        this.IE = IE;
    }

    /*
     * Completar mais tarde
     */
    @Override
    public String toString() {
        return "";
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

    /**
     * @return the cnpj
     */
    public String getCnpj() {
        return cnpj;
    }

    /**
     * @param cnpj the cnpj to set
     */
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    /**
     * @return the IE
     */
    public String getIE() {
        return IE;
    }

    /**
     * @param IE the IE to set
     */
    public void setIE(String IE) {
        this.IE = IE;
    }
    
    
}
