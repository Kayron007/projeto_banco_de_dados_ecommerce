package com.mycompany.ecommercebd;

import model.conexao;

public class EcommerceBD {

    public static void main(String[] args) {
        conexao con = new conexao();
        con.conectar(); //teste de conexao com o BD 
    }
}
