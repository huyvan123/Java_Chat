/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Control;

import Client.View.ChangePassword;
import Client.View.UserInfo;
import Model.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 *
 * @author HuyVan
 */
public class ViewDetailThread extends Thread implements ActionListener {

    private UserInfo uview;
    private ChangePassword passview;
    private User user;
    private InOutData inout;
    private boolean run = true;
    private int check = 100;

    public ViewDetailThread() {
    }

    public ViewDetailThread(UserInfo u, User user, InOutData inout) {
        this.uview = u;
        this.user = user;
        this.inout = inout;
        uview.getjButton1().addActionListener(this);
        uview.getjButton2().addActionListener(this);
        uview.getjButton3().addActionListener(this);
        chay();
    }

    private void chay() {
        this.start();
    }

    @Override
    synchronized public void run() {
        try {
            while (run) {
                int check1 = this.getCheck();
                System.out.println("check 1 in view detail: " + check1);
                switch (check1) {
                    case 100:
                        System.out.println("username :" + user.getUsername());
                        uview.set(user);
                        setCheck(0);
                        wait();
                        break;
                    case 101:
                        JOptionPane.showMessageDialog(uview, "Password incorrect!");
                        setCheck(0);
                        wait();
                        break;
                    case 102:
                        JOptionPane.showMessageDialog(uview, "Update Password successful!!");
                        passview.dispose();
                        setCheck(0);
                        wait();
                        break;
                    default:
                        this.wait();
                        break;
                }
            }
        } catch (InterruptedException ex) {
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCheck() {
        return check;
    }

    synchronized public void sTart() {
        this.notifyAll();
    }

    public void setCheck(int check) {
        this.check = check;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "OK":
                this.uview.dispose();
                this.run = false;
                break;
            case "Update":
                User u = uview.get(user);
                System.out.println("u.name: " + u.getName());
                inout.writeObject("1103");
                inout.writeObject(u);
                JOptionPane.showMessageDialog(uview, "Update successful");
                break;
            case "Change password":
                System.out.println("vao change pass");
                this.passview = new ChangePassword();
                passview.addOKlistener(new PassListener());
                passview.addCanecllistener(new PassListener());
                passview.setVisible(true);
                break;
            default:
                break;
        }
    }

    private class PassListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "OK":
                    inout.writeObject("1202");
                    inout.writeObject(passview.getjTextField1().getText());
                    inout.writeObject(passview.getjTextField2().getText());
                    inout.writeObject(passview.getjTextField3().getText());
                    break;
                case "Cancel":
                    passview.dispose();
                    break;
                default:
                    break;
            }
        }
    }

}
