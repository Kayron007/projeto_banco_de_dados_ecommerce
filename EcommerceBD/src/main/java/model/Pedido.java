/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.time.LocalDateTime;

public class Pedido extends EntidadeBase{
    private LocalDateTime data;
    private String status;
    private int valorTotal;
    private Cliente id_cliente;
    private Pagamento id_pagamento;

    public Pedido() {
        super();
    }

    public Pedido(Long id, LocalDateTime data, String status, int valorTotal, Cliente id_cliente, Pagamento id_pagamento) {
        super(id);
        this.data = data;
        this.status = status;
        this.valorTotal = valorTotal;
        this.id_cliente = id_cliente;
        this.id_pagamento = id_pagamento;
    }

    /*
     * Completar mais tarde
     */
    @Override
    public String toString() {
        return "";
    }

    /**
     * @return the data
     */
    public LocalDateTime getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(LocalDateTime data) {
        this.data = data;
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
     * @return the id_pagamento
     */
    public Pagamento getId_pagamento() {
        return id_pagamento;
    }

    /**
     * @param id_pagamento the id_pagamento to set
     */
    public void setId_pagamento(Pagamento id_pagamento) {
        this.id_pagamento = id_pagamento;
    }
    
    
}
