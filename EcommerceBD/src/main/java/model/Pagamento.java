/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author gustavo
 */
public class Pagamento extends EntidadeBase{
    private String formaDePagamento;
    private String status;
    
    public Pagamento() {
        super();
    }
    
    public Pagamento(Long id, String formaDePagamento, String status){
        super(id);
        this.formaDePagamento = formaDePagamento;
        this.status = status;
    }

    /*
     * Completar mais tarde
     */
    @Override
    public String toString() {
        return "";
    }

    /**
     * @return the formaDePagamento
     */
    public String getFormaDePagamento() {
        return formaDePagamento;
    }

    /**
     * @param formaDePagamento the formaDePagamento to set
     */
    public void setFormaDePagamento(String formaDePagamento) {
        this.formaDePagamento = formaDePagamento;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
