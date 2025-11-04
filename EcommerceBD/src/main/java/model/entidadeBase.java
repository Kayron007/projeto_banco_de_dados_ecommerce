package model;

public abstract class entidadeBase {
    private int id;

    //Construtor sem arg
    public entidadeBase() {}

    //Construtor principal
    public entidadeBase(int id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
}