
package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexao {
    private static final String url = "jdbc:mysql://localhost:3306/ECOM";
    private static final String user = "root";
    private static final String senha = "26841379";
    
    public static Connection conectar(){
        Connection conexao = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexao = DriverManager.getConnection(url, user, senha); 
            System.out.println("conexao efetuada com sucesso");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver do BD n foi encontrado" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("ocorreu um erro ao acessar o banco: " + e.getMessage());
        }
        return conexao;
    }
}
