/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

/**
 *
 * @author gustavo
 */
@SpringBootApplication(scanBasePackages = {"view", "control"})
public class MainSpring {

    public static void main(String[] args) {
        SpringApplication.run(MainSpring.class, args);
    }
}
