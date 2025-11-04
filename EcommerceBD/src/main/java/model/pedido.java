/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.time.LocalDate;

public class pedido extends entidadeBase{
    private LocalDate data;
    private String status;
    private int valorTotal;
    private cliente id_cliente;
    private pagamento id_pagamento;

    public pedido() {
    }

    public pedido(LocalDate data, String status, int valorTotal, cliente id_cliente, pagamento id_pagamento, int id) {
        super(id);
        this.data = data;
        this.status = status;
        this.valorTotal = valorTotal;
        this.id_cliente = id_cliente;
        this.id_pagamento = id_pagamento;
    }

    /**
     * @return the data
     */
    public LocalDate getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(LocalDate data) {
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
     * @return the id_pagamento
     */
    public pagamento getId_pagamento() {
        return id_pagamento;
    }

    /**
     * @param id_pagamento the id_pagamento to set
     */
    public void setId_pagamento(pagamento id_pagamento) {
        this.id_pagamento = id_pagamento;
    }
    
    
}
