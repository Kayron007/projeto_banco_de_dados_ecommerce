/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommercebd.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mycompany.ecommercebd.model.Cliente;

import jakarta.servlet.http.HttpSession;

@Controller 
public class ControllerIndex {
    
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        Cliente logado = (Cliente) session.getAttribute("clienteLogado");
        model.addAttribute("clienteLogado", logado);
        return "index";
    }

    @GetMapping("/minhaConta")
    public String minhaConta(HttpSession session, Model model) {
        Cliente c = (Cliente) session.getAttribute("clienteLogado");

        if (c == null) {
            return "redirect:/"; // ou volta para login
        }

        model.addAttribute("cliente", c);
        return "minhaConta";
    }

   @GetMapping("/masculino")
    public String masculino() {
        return "masculino";   // carrega templates/masculino.html
    }

    @GetMapping("/feminino")
    public String feminino() {
        return "feminino";    // carrega templates/feminino.html
    }
    
   @GetMapping("/acessorios")
    public String acessorios() {
    return "acessorios"; // carrega templates/acessorios.html
}

    
    @GetMapping("/promocoes")
    public String promocoes(){
        return "promocoes";
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
