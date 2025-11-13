/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author gustavo
 */
public class NotaFiscal extends EntidadeBase{
    private LocalDateTime dataDeEmissao;
    private int valorTotal;
    private int imposto;
    private String chaveDeAcesso;
    private Pedido id_pedido;

    public NotaFiscal() {
        super();
    }

    public NotaFiscal(LocalDateTime dataDeEmissao, int valorTotal, int imposto, String chaveDeAcesso, Pedido id_pedido) {
        super();
        this.dataDeEmissao = dataDeEmissao;
        this.valorTotal = valorTotal;
        this.imposto = imposto;
        this.chaveDeAcesso = chaveDeAcesso;
        this.id_pedido = id_pedido;
    }

    public NotaFiscal(Long id, LocalDateTime dataDeEmissao, int valorTotal, int imposto, String chaveDeAcesso, Pedido id_pedido) {
        super(id);
        this.dataDeEmissao = dataDeEmissao;
        this.valorTotal = valorTotal;
        this.imposto = imposto;
        this.chaveDeAcesso = chaveDeAcesso;
        this.id_pedido = id_pedido;
    }

    /* MÉTODOS VERIFICADORES */

    /*
     * Valida todos os dados do Produto
     * Lança exceções com mensagens específicas para cada erro;
     */
    public void verificar() {
        verificarDataDeEmissao();
        verificarValorTotal();
        verificarImposto();
        verificarChaveDeAcesso();
        verificarIdPedido();
    }

    /*
     * Valida a data de emissão
     * Retorna uma exceção caso o LocalDateTime não tenha sido gerado corretamente;
     */
    private void verificarDataDeEmissao() {
        if(dataDeEmissao == null) {
            throw new IllegalArgumentException("ERRO: Não foi possível gerar a data de emissão da nota fiscal!");
        }
    }

    /*
     * Valida o valor total da Nota Fiscal
     * Verifica se o resultado do cálculo é menor que zero (refletindo o requisito banco de dados);
     */
    private void verificarValorTotal() {
        if(valorTotal < 0) {
            throw new IllegalArgumentException("ERRO: Não foi possível gerar o valor total do pedido!");
        }
    }

    /*
     * Valida o valor do imposto da Nota Fiscal
     * Verifica se o resultado do cálculo é menor que zero (refletindo o requisito no banco de dados);
     */
    private void verificarImposto() {
        if(imposto < 0) {
            throw new IllegalArgumentException("ERRO: Não foi possível gerar o valor do imposto!");
        }
    }

    /*
     * Valida a chave de acesso
     * Retorna uma exceção caso a String não tenha sido gerada corretamente;
     */
    private void verificarChaveDeAcesso() {
        if(chaveDeAcesso == null || chaveDeAcesso.trim().isEmpty()) {
            throw new IllegalArgumentException("ERRO: Não foi possível gerar a chave de acesso do pedido!");
        }
    }

    /*
     * Valida o id_pedido
     * Retorna uma exceção caso o id_pedido não exista/não foi gerado corretamente;
     */
    private void verificarIdPedido() {
        if(id_pedido == null) {
            throw new IllegalArgumentException("ERRO: ID do pedido não encontrado!");
        }
    }

    /* MÉTODO DE NORMALIZAÇÃO DE DADOS */

    /**
     * Normaliza os dados dos clientes (formata, remove espaços e converte para letras maiúsculas/minúsculas)
     * Chamado antes de salvar no banco;
     */
    public void normalizar() {
        /*
         * Remove os espaços extras na chave de acesso;
         */
        if(chaveDeAcesso != null) {
            chaveDeAcesso = chaveDeAcesso.trim();
        }
    }

    /*
     * Exibe os dados da Nota Fiscal;
     */
    @Override
    public String toString() {
        return "Nota Fiscal {" +
        "\nID do pedido: " + id_pedido +
        "\nData de emissão: " + dataDeEmissao +
        "\nImposto: " + imposto +
        "\nValor total: " + valorTotal +
        "\nChave de acesso: " + chaveDeAcesso +
        "\n }";
    }

    /**
     * @return the dataDeEmissao
     */
    public LocalDateTime getDataDeEmissao() {
        return dataDeEmissao;
    }

    /**
     * @param dataDeEmissao the dataDeEmissao to set
     */
    public void setDataDeEmissao(LocalDateTime dataDeEmissao) {
        this.dataDeEmissao = dataDeEmissao;
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
     * @return the imposto
     */
    public int getImposto() {
        return imposto;
    }

    /**
     * @param imposto the imposto to set
     */
    public void setImposto(int imposto) {
        this.imposto = imposto;
    }

    /**
     * @return the chaveDeAcesso
     */
    public String getChaveDeAcesso() {
        return chaveDeAcesso;
    }

    /**
     * @param chaveDeAcesso the chaveDeAcesso to set
     */
    public void setChaveDeAcesso(String chaveDeAcesso) {
        this.chaveDeAcesso = chaveDeAcesso;
    }

    /**
     * @return the id_pedido
     */
    public Pedido getId_pedido() {
        return id_pedido;
    }

    /**
     * @param id_pedido the id_pedido to set
     */
    public void setId_pedido(Pedido id_pedido) {
        this.id_pedido = id_pedido;
    }   
}