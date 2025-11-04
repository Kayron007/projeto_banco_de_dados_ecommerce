/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author gustavo
 */
public class pessoaJuridica extends entidadeBase{
    private cliente id_cliente;
    private String cnpj;
    private String IE;

    public pessoaJuridica() {
    }

    public pessoaJuridica(cliente id_cliente, String cnpj, String IE, int id) {
        super(id);
        this.id_cliente = id_cliente;
        this.cnpj = cnpj;
        this.IE = IE;
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
