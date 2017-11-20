/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Run;

import Server.Ctrl.Server;
import Server.View.View;

/**
 *
 * @author HuyVan
 */
public class Run {
    public static void main(String[] args) {
        View view = new View();  
        view.setVisible(true);
        Server server = new Server(view);
        
    }
}
