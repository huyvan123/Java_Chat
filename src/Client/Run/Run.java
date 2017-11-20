/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Run;

import Client.View.ViewLogin;
import Client.Control.Login;
import java.io.IOException;

/**
 *
 * @author HuyVan
 */
public class Run {
    public static void main(String[] args) throws IOException {
        ViewLogin view = new ViewLogin();
        Login log = new Login(view);
        view.setVisible(true);
    }
}
