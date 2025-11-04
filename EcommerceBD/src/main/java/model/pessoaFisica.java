/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author gustavo
 */
public class pessoaFisica extends entidadeBase{
    private cliente id_cliente;
    private String cpf;
    private String rg;

    public pessoaFisica() {
    }

    public pessoaFisica(cliente id_cliente, String cpf, String rg, int id) {
        super(id);
        this.id_cliente = id_cliente;
        this.cpf = cpf;
        this.rg = rg;
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
     * @return the cpf
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * @param cpf the cpf to set
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * @return the rg
     */
    public String getRg() {
        return rg;
    }

    /**
     * @param rg the rg to set
     */
    public void setRg(String rg) {
        this.rg = rg;
    }
}
