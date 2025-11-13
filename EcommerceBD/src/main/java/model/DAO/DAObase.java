/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.DAO;

import java.sql.SQLException;
import java.util.List;

public interface DAObase <T> {
    public void inserir(T obj)  throws SQLException;
    
    public void deletar(T obj);
    
    public void alterar(T obj);
    
    public List <T> listar();
    
    T buscarPorId(Long id) throws SQLException ;
}
