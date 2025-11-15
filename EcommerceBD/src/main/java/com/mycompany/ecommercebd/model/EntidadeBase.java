package com.mycompany.ecommercebd.model;

public abstract class EntidadeBase {
    protected Long id;

    //Construtor sem arg
    public EntidadeBase() {}

    //Construtor principal
    public EntidadeBase(Long id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    public abstract String toString();
}