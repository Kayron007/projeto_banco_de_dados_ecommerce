package com.mycompany.ecommercebd;

import model.Conexao;

public class EcommerceBD {

    public static void main(String[] args) {
        Conexao con = new Conexao();
        con.conectar(); //teste de conexao com o BD 
    }
}
