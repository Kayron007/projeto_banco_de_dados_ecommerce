/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.util.Scanner;
import control.*;

/**
 *
 * @author gustavo
 */
public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE E-COMMERCE                ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        clienteControl clienteControl = new clienteControl(scan);
        clienteControl.menu(scan);
        
        System.out.println("\nSistema encerrado.");
        scan.close();
    }
}
