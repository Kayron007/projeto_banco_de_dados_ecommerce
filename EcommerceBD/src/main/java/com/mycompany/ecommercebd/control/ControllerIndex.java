/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommercebd.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mycompany.ecommercebd.model.Cliente;

@Controller 
public class ControllerIndex {
    
    @GetMapping("/")
    public String index(Model model){
        Cliente cliente = new Cliente("fisico", "nome", "email", "senha", "11111111", "cidade", "logradouro", "numero", "bairro", "estado");
        System.out.println("Metodo index foi chamado");
        model.addAttribute("mensagem", "Bem-vindo ao E-commerce!");
        return "index";
    }
    
    @GetMapping("/masculino")
    public String masculino(){
        return "produtos";
    }
    
    @GetMapping("/feminino")
    public String feminino(){
        return "produtos";
    }
    
    @GetMapping("/acessorios")
    public String acessorios(){
        return "produtos";
    }
    
    @GetMapping("/promocoes")
    public String promocoes(){
        return "produtos";
    }
    @GetMapping("/carrinho")
    public String carrinho(){
        return "carrinho";
    }
    @GetMapping("/produtos")
    public String produtos(){
        return "produtos";
    }
    @GetMapping("/checkout")
    public String checkout(){
        return "checkout";
    }    
}
