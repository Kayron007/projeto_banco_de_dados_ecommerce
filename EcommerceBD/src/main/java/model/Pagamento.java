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
    private boolean status = false;
    
    /*
     * Construtor padrão
     * Utilizado para criar um Pagamento vazio que será populado depois;
     */
    public Pagamento() {
        super();
    }
    
    /*
     * Construtor completo para novo Pagamento
     * Utilizado para gerar um novo Pagamento
     * O ID será gerado automáticamente pelo método gerarIdUnico;
     */
    public Pagamento(String formaDePagamento, boolean status) {
        super();
        this.formaDePagamento = formaDePagamento;
        this.status = status;
    }

    /*
     * Construtor completo para Pagamento já existente;
     */
    public Pagamento(Long id, String formaDePagamento, boolean status){
        super(id);
        this.formaDePagamento = formaDePagamento;
        this.status = status;
    }

    public void validar() {
        validarFormaDePagamento();
    }

    private void validarFormaDePagamento() {
        if(formaDePagamento != null || formaDePagamento.trim().isEmpty()) {
            throw new IllegalArgumentException("Selecione uma forma de pagamento!");
        } else {
            status = true;
        }
    }

    /*
     * Exibe os dados do Pagamento;
     */
    @Override
    public String toString() {
        return "Pagamento {" +
        "\nForma de pagamento: " + formaDePagamento +
        "\nStatus do pagamento: " + (isStatus() ? "Pago" : "Não pago") +
        " }";
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
    public boolean isStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(boolean status) {
        this.status = status;
    } 
}
