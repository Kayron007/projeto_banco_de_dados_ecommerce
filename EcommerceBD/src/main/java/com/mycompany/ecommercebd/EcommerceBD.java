/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommercebd;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

/**
 *
 * @author gustavo
 */
@SpringBootApplication(scanBasePackages = {"model", "control"})
public class EcommerceBD {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceBD.class, args);
    }
}