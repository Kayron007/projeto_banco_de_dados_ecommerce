/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author gustavo
 */
public class Avaliacao extends EntidadeBase{
    private Short nota;
    private String comentario;
    private Produto id_produto;
    private Cliente id_cliente;

    /*
     * Construtor padrão 
     * Utilizado para criar uma Avaliação vazia que será populada depois;
     */
    public Avaliacao() {
        super();
    }

    /*
     * Construtor completo para nova Avaliação
     * Utilizado para gerar uma nova avaliação do serviço
     * O ID da avaliação será gerado automáticamente pelo método gerarIdUnico;
     */
    public Avaliacao(Short nota, String comentario, Produto id_produto, Cliente id_cliente) {
        super();
        this.nota = nota;
        this.comentario = comentario;
        this.id_produto = id_produto;
        this.id_cliente = id_cliente;
    }

    /* 
     * Construtor completo para retornar avaliação já existente;
     */
    public Avaliacao(Long id, Short nota, String comentario, Produto id_produto, Cliente id_cliente) {
        super(id);
        this.nota = nota;
        this.comentario = comentario;
        this.id_produto = id_produto;
        this.id_cliente = id_cliente;
    }

    /* MÉTODOS DE VALIDAÇÃOS */
    /*
     * Valida os dados da avaliação
     * Lança exceções com mensagens específicas para cada erro;
     */

    public void validar() {
        validarComentario();
        validarIdProduto();
        validarIdCliente();
    }

    /*
     * Valida o comentário da avaliação, garantindo que não tenha mais de 500 caracteres (limite do banco);
     */
    private void validarComentario() {
        if(comentario != null && comentario.trim().length() > 500) {
            throw new IllegalArgumentException("O limite de caracteres é 500!");
        }
    }

    private void validarIdProduto() {
        if(nota != null && id_produto == null) {
            throw new IllegalArgumentException("ERRO: ID do produto não encontrado!");
        }
    }

    private void validarIdCliente() {
        if(nota != null && id_cliente == null) {
            throw new IllegalArgumentException("ERRO: ID do cliente não encontrado!");
        }
    }

    /* MÉTODO DE NORMALIZAÇÃO */

    /**
     * Normaliza os dados dos clientes (formata, remove espaços e converte para letras maiúsculas/minúsculas)
     * Chamado antes de salvar no banco;
     */
    public void normalizar() {

        /*
         * Remove espaços extras no comentário da avaliação;
         */
        if(comentario != null) {
            comentario = comentario.trim();
        }
    }

    /*
     * Exibe os dados da avaliação;
     */
    @Override
    public String toString() {
        return "Avaliação {" +
        "\nNota: " + nota +
        "\nComentário: " + comentario +
        "\nProduto: " + id_produto +
        "\nCliente: " + id_cliente +
        " }";
    }

    /**
     * @return the nota
     */
    public short getNota() {
        return nota;
    }

    /**
     * @param nota the nota to set
     */
    public void setNota(short nota) {
        this.nota = nota;
    }

    /**
     * @return the comentario
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * @param comentario the comentario to set
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    /**
     * @return the id_produto
     */
    public Produto getId_produto() {
        return id_produto;
    }

    /**
     * @param id_produto the id_produto to set
     */
    public void setId_produto(Produto id_produto) {
        this.id_produto = id_produto;
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
}

