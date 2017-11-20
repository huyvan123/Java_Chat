/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Control;

import Client.View.MainView;
import Client.View.ViewLogin;
import Model.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author HuyVan
 */
public class Login {

    private Socket client;
    private ViewLogin view;
    private InOutData inout;
    private User user;

    public Login(ViewLogin log) {
        this.view = log;
        try {
            this.client = new Socket("localhost", 2222);
            this.inout = new InOutData(client);
        } catch (IOException ex) {
        }
        this.view.addLoginListener(new LoginListener());
        this.view.addSignUpListener(new signUpListener());
    }

    class signUpListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                user = view.getUser();
                inout.writeObject("11");
                inout.writeObject(user);
                String check = (String) inout.readObject();
                if (check.compareTo("1") == 0) {
                    view.showMess("Sigup successful");
                    view.dispose();
                    MainView v = new MainView();
                    Main main = new Main(inout, v);
                    v.setVisible(true);
                } else {
                    view.showMess("Username is available!");
                }
            } catch (IOException | ClassNotFoundException ex) {
            }
        }
    }

    class LoginListener implements ActionListener {

        @Override
       synchronized public void actionPerformed(ActionEvent e) {
            try {
                user = view.getUser();
                inout.writeObject("10");
                inout.writeObject(user);
                this.wait(10);
                String check = (String) inout.readObject();
                if (check.compareTo("1") == 0) {
                    view.showMess("successful");
                    view.dispose();
                    MainView v = new MainView();
                    Main main = new Main(inout, v);
                    v.setVisible(true);
                } else {
                    view.showMess("Username is unavailable!");
                }
            } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            }
        }
    }

}
