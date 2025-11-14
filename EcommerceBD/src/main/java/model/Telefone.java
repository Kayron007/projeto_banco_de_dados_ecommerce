/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author gustavo
 */
public class Telefone extends EntidadeBase{
    private String numero;
    private String tipo;
    private Fornecedor id_fornecedor;
    private Cliente id_cliente;

    /*
     * Construtor padrão
     * Utilizado para criar um Telefone vazio que será populado depois
     */
    public Telefone() {
        super();
    }

    /*
     * Construtor completo para Telefone não cadastrado
     * Utilizado cadastrar um novo Telefone
     * O ID será gerado automáticamente pelo método gerarIdUnico;
     */
    public Telefone(String numero, String tipo, Fornecedor id_fornecedor, Cliente id_cliente) {
        super();
        this.numero = numero;
        this.tipo = tipo;
        this.id_fornecedor = id_fornecedor;
        this.id_cliente = id_cliente;
    }

    /*
     * Construtor completo para Telefone já cadastrado;
     */
    public Telefone(Long id, String numero, String tipo, Fornecedor id_fornecedor, Cliente id_cliente) {
        super(id);
        this.numero = numero;
        this.tipo = tipo;
        this.id_fornecedor = id_fornecedor;
        this.id_cliente = id_cliente;
    }

    /* MÉTODOS DE VALIDAÇÃO */

    /*
     * Valida todos os dados do Telefone
     * Lança exceções com mensagens específicas para cada erro;
     */
    public void validar() {
        validarId();
        validarNumero();
        validarTipo();
    }

    /*
     * Verifica se:
     * O telefone pertence a um cliente ou a um fornecedor
     * Retorna exceção caso ambos sejam nulos ou já estejam preenchidos;
     */
    private void validarId() {
        if((id_cliente == null && id_fornecedor == null) || (id_cliente != null && id_fornecedor != null)) {
            throw new IllegalArgumentException("ERRO: Telefone deve estar vinculado a uma conta!");
        }
    }

    /*
     * Verifica se o número existe ou foi preenchido e se contém mais/menos que 11 caracteres;
     */
    private void validarNumero() {
        if(numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("ERRO: O campo 'Número' é obrigatório!");
        }

        if(numero.length() != 11) {
            throw new IllegalArgumentException("ERRO: O número de telefone deve conter 11 caracteres!");
        }
    }

    /*
     * Verifica se o tipo existe ou foi preenchido;
     */
    private void validarTipo() {
        if(tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("ERRO: O campo 'Tipo' é obrigatório!");
        }
    }

    /* MÉTODO DE NORMALIZAÇÃO */

    /*
     * Normaliza os dados do Telefone
     * Chamado antes de salvar no banco;
     */
    public void normalizar() {

        /*
         * Remove os espaços extras e a máscara do número;
         */
        if(numero != null) {
            numero = numero.trim();
            numero = numero.replaceAll("[^0-9]", "");
        }

        /*
         * Remove os espaços extras do tipo;
         */
        if(tipo != null) {
            tipo = tipo.trim();
        }
    }
    
    /*
     * Exibe os dados do Telefone;
     */
    @Override
    public String toString() {

        /*
         * Chama o método validarId para definir se a mensagem será retornada a um cliente ou fornecedor;
         */
        try {
            validarId();
        } catch (Exception e) {
            System.out.println("ERRO: A validação do ID não pode ser concluída! " + e.getMessage());
        }
        String dono;

        if(id_cliente != null) {
            dono = "Cliente: " + id_cliente.getId();
        } else {
            dono = "Fornecedor: " + id_fornecedor.getId();
        }

        return "Telefone: {" +
        "\nID: " + getId() +
        "\nNúmero: " + numero +
        "\nTipo: " + tipo +
        "\nDono: " + dono +
        "\n}"; 
    }

    /**
     * @return the numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the id_fornecedor
     */
    public Fornecedor getId_fornecedor() {
        return id_fornecedor;
    }

    /**
     * @param id_fornecedor the id_fornecedor to set
     */
    public void setId_fornecedor(Fornecedor id_fornecedor) {
        this.id_fornecedor = id_fornecedor;
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
