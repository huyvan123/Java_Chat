/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Control;

import Client.View.MemberOption;
import Model.Friend;
import Model.User;
import Server.Model.SVFriend;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 *
 * @author HuyVan
 */
public class AddFriendThread extends Thread implements ActionListener {

    private final MemberOption memview;
    private final User user;
    private final User user2;
    private final InOutData inout;
    private boolean run = true;
    private int check = 100;

    public AddFriendThread(MemberOption memview, User user, InOutData inout,User user2) {
        this.memview = memview;
        this.user = user;
        this.user2 = user2;
        this.inout = inout;
        this.memview.getjButton1().addActionListener(this);
        this.memview.getjButton2().addActionListener(this);
        this.start();
    }

    @Override
    synchronized public void run() {
        try {
            while (run) {
                int check1 = this.getCheck();
                switch (check1) {
                    case 100:
                        memview.set(user2);
                        setCheck(0);
                        wait();
                        break;
                    case 101:
                        wait();
                        break;
                    case 102:
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

    synchronized public void sTart() {
        this.notifyAll();
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            this.memview.setVisible(false);
            //this.memview.dispose();
        } else {
            if(checkExist()){
                JOptionPane.showMessageDialog(null, "Đã kết bạn với người này!");
            }else{
            inout.writeObject("107");
            inout.writeObject(user2);
            }
        }
    }

    private boolean checkExist(){
        SVFriend f = new SVFriend(new Friend(user, user2));
        f.connect();
        boolean b = f.checkExist();
        f.close();
        return b;
    }
}
