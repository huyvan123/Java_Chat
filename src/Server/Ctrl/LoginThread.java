/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Ctrl;

import java.net.Socket;
import Model.User;
import Server.Model.SVUser;

/**
 *
 * @author HuyVan
 */
public class LoginThread extends Thread {

    private final Server server;
    private final Socket client;
    private SVUser user;
    private final boolean run;
    private final InOutData inout;
    private Chat chat;

    public LoginThread(Server server, Socket client) {
        this.server = server;
        this.client = client;
        inout = new InOutData(this.client);
        run = true;
        batdau();
    }

    private void batdau() {
        this.start();
    }

    public void sendMes(Object mess) {
        inout.writeObject(mess);
    }

    public Chat getChat() {
        return chat;
    }

    public void sendOnl() {
        user.connect();
        user.getFriendFromDB();
        user.closeConnect();
        server.setOnline(user);
        this.sendMes(1000);
        this.sendMes(changeUser(user));
    }

    @Override
    synchronized public void run() {
        while (run) {
            try {
                int i = Integer.parseInt((String) inout.readObject());
                user = new SVUser((User) inout.readObject());
                user.connect();
                if (i == 10) {
                    if (user.checkLoginUser() && server.checkConnect(user)) {
                        user.closeConnect();
                        server.sendRequestDestroy(user);
                        this.wait(900);
                        inout.writeObject("1");
                        server.addThread(this, user);
                        this.chat = new Chat(server, user, inout);
                    } else if (user.checkLoginUser() && !server.checkConnect(user)) {
                        user.closeConnect();
                        inout.writeObject("1");
                        server.addThread(this, user);
                        this.chat = new Chat(server, user, inout);
                    } else {
                        inout.writeObject("0");
                        user.closeConnect();
                    }
                } else if (i == 11) {
                    if (!user.checkRegUser()) {
                        user.addUserIntoDB();
                        user.closeConnect();
                        inout.writeObject("1");
                        server.addThread(this, user);
                        chat = new Chat(server, user, inout);
                    } else {
                        inout.writeObject("0");
                        user.closeConnect();
                    }
                }
                user.closeConnect();
                this.wait(100);
            } catch (NumberFormatException | InterruptedException ex) {
            }
        }
    }

    private User changeUser(SVUser u) {
        User us = new User(u.getUsername(), u.getPassword(), u.getImage(), u.getStatus(), u.getOnline(),
                u.getIdPerson(), u.getName(), u.getAddress(), u.getDob(), u.getSdt());
        us.setListFriend(u.getListFriend());
        us.setListRoom(u.getListRoom());
        return us;
    }
}
