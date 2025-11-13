///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package view;
//
//import java.util.Scanner;
//import java.sql.Connection;
//import control.*;
//import model.Conexao;
//
///**
// *
// * @author gustavo
// */
//public class Main {
//    public static void main(String[] args) {
//        Scanner scan = new Scanner(System.in);
//
//        System.out.println("╔════════════════════════════════════════╗");
//        System.out.println("║   SISTEMA DE E-COMMERCE                ║");
//        System.out.println("╚════════════════════════════════════════╝");
//        
//        try {
//            Connection conn = Conexao.conectar();
//            ClienteControl clienteControl = new ClienteControl(conn);
//            clienteControl.menu(scan);
//            conn.close();
//        } catch (Exception e) {
//            System.err.println("Erro ao conectar ao banco: " + e.getMessage());
//        } finally {
//            scan.close();
//        }
//        
//        System.out.println("\nSistema encerrado.");
//    }
//}
