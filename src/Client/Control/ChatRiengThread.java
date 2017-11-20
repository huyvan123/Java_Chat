/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Control;

import Client.View.ChatRieng;
import Model.Message;
import Model.Room;
import Model.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author HuyVan
 */
public class ChatRiengThread extends Thread {

    private InOutData inout;
    private User user;
    private ChatRieng view;
    private boolean run;
    private int check = 0;
    private Room room;
    private Message mess;

    public ChatRiengThread() {
    }

    public ChatRiengThread(InOutData inout, User user, ChatRieng chatr, Room r) {
        this.inout = inout;
        this.user = user;
        this.view = chatr;
        this.room = r;
        run = true;
        chatr.addChatListener(new ChatListener());
        chatr.addExitChatListener(new ChatListener());
        batdau();
    }
    
    private void batdau(){
        this.start();
    }

    @Override
    synchronized public void run() {
        try {
            while (run) {
                int check1 = this.getCheck();
                switch (check1) {
                    case 100:
                        String mes = this.mess.getMess();
                        view.addMess(mes + "\n");
                        this.wait();
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

    public ChatRieng getView() {
        return view;
    }

    public void setCheck(int c) {
        this.check = c;
    }

    public int getCheck() {
        return this.check;
    }

    public Message getMess() {
        return mess;
    }

    public void setMess(Message mess) {
        this.mess = mess;
    }

    private class ChatListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("actioncomand: " + e.getActionCommand());
            if (e.getActionCommand().compareTo("Exit") == 0) {
                view.setVisible(false);
            } else {
                String mess = view.getMess();
                view.addMess("Me: " + mess + "\n");
                inout.writeObject("104");
                inout.writeObject(mess);
                inout.writeObject(room);
            }
        }
    }
}
