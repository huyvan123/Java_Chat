/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Ctrl;

import Server.Model.Connect;
import Server.View.View;
import Model.*;
import Server.Model.SVRoom;
import Server.Model.SVUser;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author HuyVan
 */
public class Server {

    public View view;
    private ServerSocket server;
    private ArrayList<SVRoom> listroom;//danh sách các phòng
    private ArrayList<Connect> listconnect;//danh sách thread của các user

    public Server(View view) {
        super();
        this.view = view;
        start();
    }

    private void start() {
        view.getjTextArea1().append("may chu san sang!\n");
        go();
    }

    public void go() {
        try {
            server = new ServerSocket(2222);
            view.getjTextArea1().append("máy chủ đang phục vụ!\n");
            listroom = new ArrayList<>();
            listconnect = new ArrayList<>();
            while (true) {
                Socket client = server.accept();

                LoginThread loginThread = new LoginThread(this, client);

            }
        } catch (IOException e) {
        }
    }

    public void destroy(SVUser user) {
        int index;
        for (Connect c : listconnect) {
            if (c.getUser().getUsername().equals(user.getUsername())) {
                index = listconnect.indexOf(c);
                listconnect.remove(index);
                c.getLoginthread().interrupt();
                break;
            }
        }
    }

    public void sendDisconnect(ArrayList<Room> list) {
        for (Connect c : listconnect) {
            c.getLoginthread().sendMes(1010);
            c.getLoginthread().sendMes(list);
        }
    }

    public void sendRequestDestroy(SVUser user) {
        for (Connect c : listconnect) {
            if (c.getUser().getUsername().equals(user.getUsername())) {
                c.getLoginthread().sendMes(2010);
            }
        }
    }

    public boolean checkConnect(SVUser user) {
        for (Connect c : listconnect) {
            if (c.getUser().getUsername().equals(user.getUsername())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<SVRoom> getListRoom() {
        return listroom;
    }

    public boolean checkRoom(int idroom) {
        for (SVRoom a : listroom) {
            System.out.println("id r: " + a.getIdRoom());
            if (a.getIdRoom() == idroom) {
                return true;
            }
        }
        return false;
    }

    public void addRoom(SVRoom room) {
        this.listroom.add(room);
    }

    public SVRoom getRoom(int id) {
        for (SVRoom r : this.listroom) {
            if (r.getIdRoom() == id) {
                return r;
            }
        }
        return null;
    }

    public void addThread(LoginThread thread, SVUser user) {
        this.listconnect.add(new Connect(thread, user));
    }

    public ArrayList<Connect> getListConnect() {
        return listconnect;
    }

    public void removeUserRoom(SVUser user, int idroom) {
        SVRoom r = this.getRoom(idroom);
        for (User u : r.getListUser()) {
            if (u.getUsername().equals(user.getUsername())) {
                int indext = r.getListUser().indexOf(u);
                r.deleteUserFromList(r.getListUser().get(indext));
                break;
            }

        }
    }

    public void setOnline(SVUser user) {
        for (Connect l : listconnect) {
            for (User f : user.getListFriend()) {
                if (f.getUsername().compareTo(l.getUser().getUsername()) == 0) {
                    f.setOnline(1);
                }
            }
        }
    }

}
