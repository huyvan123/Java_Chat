/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author HuyVan
 */
public class DAOConnect {
    
    private Connection connect;

    public DAOConnect() {
        String url = "jdbc:mysql://localhost:3306/user";
        String dbClass = "com.mysql.jdbc.Driver";
        
        try {
               Class.forName(dbClass);
               connect = DriverManager.getConnection(url,"root","van123");
        } catch (ClassNotFoundException | SQLException e) {
        }
    }
    
    public Connection getConnect(){
        return this.connect;
    }
}
