/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Ctrl;

import Server.Model.Connect;
import Model.*;
import Server.Model.Room_User;
import Server.Model.SVFriend;
import Server.Model.SVMess;
import Server.Model.SVRoom;
import Server.Model.SVUser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author HuyVan
 */
public final class Chat {

    private Server server;
    private SVUser user;
    private InOutData inout;
    private SVRoom room;
    private SVRoom roomAll;
    private boolean run;
    private String imagePath;
    private SVFriend friend;
    private SVMess messsv;

    public Chat() {
    }

    public Chat(Server sv, SVUser user, InOutData inout) {
        this.server = sv;
        this.user = user;
        this.inout = inout;
        this.run = true;
        sendInit();
        this.quat();
    }

    synchronized public void quat() {
        while (run) {
            try {
                int request = Integer.parseInt((String) inout.readObject());
                System.out.println("request: " + request);
                String name = this.user.getUsername();
                switch (request) {
                    case 100:
                        //chat nhóm
                        runAll(1, name);
                        break;
                    case 101:
                        //gửi mess cho chat all
                        String mess = (String) inout.readObject();
                        room = server.getRoom(1);
                        sendAllMess(room, mess);
                        break;
                    case 102:
                        //click thoat
                        ArrayList<Room> listR = (ArrayList<Room>) inout.readObject();
                        if (listR != null && !listR.isEmpty()) {
                            server.sendDisconnect(listR);
                            server.view.getjTextArea1().append(name + ": đã thoát khói phòng có id = " + 1 + "\n");
                        }
                        room = server.getRoom(1);
                        if (room != null) {
                            sendAllMess(room, name + ": đã thoát khói phòng \n");
                            server.removeUserRoom(user, 1);
                            sendOnline(server.getRoom(1));
                        }
                        user.setOnline(0);
                        server.destroy(user);
                        sendToFriend(user);
                        break;
                    case 103:
                        //chat đơn
                        SVUser user2 = new SVUser((User) inout.readObject());
                        Room_User ru = new Room_User();
                        ru.setUser1(user2);
                        ru.setUser2(user);
                        ru.connectDB();
                        int idr;
                        SVRoom rm = new SVRoom();
                        if (ru.getIdRoomChat() != null) {
                            idr = ru.getIdRoomChat();
                            ru.close();
                        } else {
                            rm.setName(user.getUsername() + "-" + user2.getUsername());
                            rm.connectDB();
                            rm.addRoomInDB();
                            int newIdRoom = rm.getLastIdRoom();
                            rm.close();
                            rm.setIdRoom(newIdRoom);
                            ru.setRoom(rm);
                            ru.addRoom_User();
                            idr = ru.getIdRoomChat();
                            ru.close();
                        }
                        rm.addUserToList(changeUser(user));
                        rm.addUserToList(changeUser(user2));
                        rm.setIdRoom(idr);
                        server.addRoom(rm);
                        rm.connectDB();
                        rm.getListMessFromDB();
                        rm.close();
                        //GỬI tin cũ cho thằng this.user
                        sendOldMess(user, rm);
                        //gửi cho thằng user2
                        //sendOldMess(user2, idroom);
                        //gửi yêu cầu của thằng 1 cho thằng 2
                        sendUser(rm, user2);
                        break;
                    case 104:
                        //gui tin nhan cho chat rieng
                        String mess1 = (String) inout.readObject();
                        room = new SVRoom((Room) inout.readObject());
                        Message messa = new Message(changeUser(user), changeRoom(room), user.getUsername() + ": " + mess1);
                        messsv = new SVMess(messa);
                        messsv.connectDB();
                        messsv.addMesInDB();
                        messsv.close();
                        //this.dao.addMess(messa);
                        //gửi 1 đối tượng message
                        sendAllMess(room, messsv);
                        break;
                    case 105:
                        //thoat chat don - khi 1 thang an thoat
                        //gui 105 để yêu cầu thằng còn lại đóng cửa sổ
                        inout.writeObject("105");
                        break;
                    case 106:
                        // khi click thoát khỏi hệ thống
                        //gửi lại list friend tới tất cả friend của mình
                        //this.sendListOnlToAllFriend();
                        run = false;
                        inout.exit();
                        break;
                    case 107:
                        //nhan yeu kết bạn tu thang yeu cau
                        SVUser uadd = new SVUser((User) inout.readObject());
                        sendThisUserToOneInListConnect(2017, uadd);
//                        inout.writeObject(9009);
//                        inout.writeObject(uadd);
//                         this.sendListOnl();
//                        this.sendListOnlToAllFriend();
                        break;
                    case 108:
                        //thay avatar
                        //nhận icon
                        ObjectInputStream in = inout.getOis();
                        ImageInputStream imin = (ImageInputStream) in.readObject();
                        // BufferedImage bi = ImageIO.read((Image)in.readObject());
                        try {
//                        fout = new FileOutputStream("Image/a1.jpg");
                        } catch (Exception e) {
                        }
                        break;
                    case 1102:
                        //nhan yeu cau view info
                        user.connect();
                        User use = user.getUserInfo();
                        user.closeConnect();
                        inout.writeObject(2202);
                        inout.writeObject(use);
                        break;
                    case 1103:
                        //nhan yeu cau update user
                        user = new SVUser((User) inout.readObject());
                        user.connect();
                        user.update();
                        User use1 = user.getUserInfo();
                        user.closeConnect();
                        inout.writeObject(2202);
                        inout.writeObject(use1);
                        sendStatus();
                        break;
                    case 1202:
                        String username = (String) inout.readObject();
                        String password = (String) inout.readObject();
                        String newpassword = (String) inout.readObject();
                        SVUser u = new SVUser(new User(username, password));
                        u.connect();
                        if (u.checkLoginUser()) {
                            u.updatePW(newpassword);
                            inout.writeObject(1211);
                        } else {
                            inout.writeObject(1212);
                        }
                        u.closeConnect();
                        break;
                    case 1030:
                        //yeu cau xem info mem trong room chat all
                        SVUser user3 = new SVUser((User) inout.readObject());
                        user3.connect();
                        User us = user3.getUserInfo();
                        user3.closeConnect();
                        inout.writeObject(1090);
                        inout.writeObject(us);
                        break;
                    case 2018:
                        //chap nhan yeu cau ket ban
                        SVUser user9 = new SVUser((User) inout.readObject());
                        sendThisUserToOneInListConnect(2020, user9);
                        break;
                    case 2121:
                        SVUser user4 = new SVUser((User) inout.readObject());
                        friend = new SVFriend(new Friend(changeUser(user4), changeUser(user)));
                        user4.connect();
                        user4.getFriendFromDB();
                        user4.closeConnect();
                        System.out.println("list truoc update friend: " + user4.getListFriend().size());
                        friend.connect();
                        friend.addFriend();
                        friend.close();
                        user4.connect();
                        user4.getFriendFromDB();
                        user4.closeConnect();
                        System.out.println("list sau update friend: " + user4.getListFriend().size());
                         {
                            try {
                                wait(100);
                            } catch (InterruptedException ex) {
                            }
                        }
                        this.sendToFriend(user4);
                         {
                            try {
                                wait(100);
                            } catch (InterruptedException ex) {
                            }
                        }
                        this.sendToFriend(user);
                        break;
                    case 2019:
                        //ko chap nhan yeu cau ket ban
                        SVUser user5 = new SVUser((User) inout.readObject());
                        sendThisUserToOneInListConnect(2021, user5);
                        break;
                    case 1111:
                        SVUser user6 = new SVUser((User) inout.readObject());
                        friend = new SVFriend(new Friend(changeUser(user), changeUser(user6)));
                        friend.connect();
                        friend.deleteDB();
                        friend.close();
                        inout.writeObject(1112);
                         {
                            try {
                                wait(100);
                            } catch (InterruptedException ex) {
                            }
                        }
                        this.sendListOnl(user6);
                         {
                            try {
                                wait(100);
                            } catch (InterruptedException ex) {
                            }
                        }
                        this.sendListOnl(user);
                        break;
                    case 9999:
//                        inout.writeObject(9000);
                        createNewConnect();
                        user.connect();
                        user.updateImage(imagePath);
                        user.closeConnect();
                        break;
                    case 9222:
                        sendImage();
                        break;
                    default:
                        break;
                    //run = false;
                }
            } catch (IOException | ClassNotFoundException | NumberFormatException ex) {
                //System.out.println("loi server!");
            }
        }
    }

    public void runAll(int idroom, String username) {
        if (server.checkRoom(idroom)) {
            server.getRoom(idroom).addUserToList(user);
        } else {
            roomAll = new SVRoom();
            roomAll.setIdRoom(idroom);
            roomAll.addUserToList(user);
            server.addRoom(roomAll);
        }
        server.view.getjTextArea1().append(user.getUsername() + " đã tham gia vào phong chat với id la: " + idroom + "\n");
        roomAll = server.getRoom(idroom);
        sendAllMess(roomAll, username + " đã tham gia phòng chat!\n");
        sendOnline(roomAll);
    }

    //hiển thị trong bảng chat chung
    private void sendAllMess(SVRoom room, String mess) {
        Iterator<Connect> list = server.getListConnect().iterator();
        while (list.hasNext()) {
            Connect t = list.next();
            if (room.checkUserInList(t.getUser()) && !t.getUser().getUsername().equals(user.getUsername())) {
                //nhận 200 để in ra bảng chat chung
                t.getLoginthread().sendMes(200);
                t.getLoginthread().sendMes(room.getIdRoom());
                t.getLoginthread().sendMes(mess);
            }
        }
    }

    private void sendAllMess(SVRoom room, SVMess mess) {
        Iterator<Connect> list = server.getListConnect().iterator();
        while (list.hasNext()) {
            Connect t = list.next();
            if (room.checkUserInList(t.getUser()) && user.getUsername().compareTo(t.getUser().getUsername()) != 0) {
                //nhận 201 để in ra bảng chat riêng
                t.getLoginthread().sendMes(201);
                t.getLoginthread().sendMes(changeRoom(room));
                t.getLoginthread().sendMes(changeMess(mess));

            }
        }
    }

    private void sendOnline(SVRoom room) {
        String s = "";
        for (User u : room.getListUser()) {
            s = s + u.getUsername() + ",";
        }
        Iterator<Connect> list = server.getListConnect().iterator();
        while (list.hasNext()) {
            Connect t = list.next();
            if (room.checkUserInList(t.getUser())) {
                // nhận 202 để in danh sách online cho chát chung
                t.getLoginthread().sendMes(202);
                t.getLoginthread().sendMes(s);
            }
        }
    }

    private void sendUser(SVRoom room, User user) {
        Iterator<Connect> list = server.getListConnect().iterator();
        while (list.hasNext()) {
            Connect c = list.next();
            if (c.getUser().getUsername().compareTo(user.getUsername()) == 0) {
                //nhận 204 để chấp nhận yêu cầu chat riêng 
                c.getLoginthread().sendMes(204);
                c.getLoginthread().sendMes(changeRoom(room));
                c.getLoginthread().sendMes(changeUser(this.user));
            }
        }
    }

    private void sendThisUserToOneInListConnect(int sendNumber, SVUser u) {
        Iterator<Connect> list = server.getListConnect().iterator();
        while (list.hasNext()) {
            Connect c = list.next();
            if (c.getUser().getUsername().compareTo(u.getUsername()) == 0) {

                c.getLoginthread().sendMes(sendNumber);
                c.getLoginthread().sendMes(changeUser(this.user));
                break;
            }
        }
    }

    private void sendOldMess(User user, SVRoom room) {
        Room r = changeRoom(room);
        Iterator<Connect> list = server.getListConnect().iterator();
        while (list.hasNext()) {
            Connect c = list.next();
            if (c.getUser().getUsername().compareTo(user.getUsername()) == 0) {
                //nhận 203 để in ra tin nhắn cũ
                c.getLoginthread().sendMes(203);
                c.getLoginthread().sendMes(r);
            }
        }
    }

    public void sendListOnl(SVUser user) {
        //Friend friend = dao.getFriend(user);
        user.connect();
        user.getFriendFromDB();
        user.closeConnect();
        if (user.getListFriend().isEmpty()) {

        } else {
            //nhận 1000 để load list friend
            server.setOnline(user);
        }
        Iterator<Connect> listC = server.getListConnect().iterator();
        while (listC.hasNext()) {
            Connect c = listC.next();
            if (c.getUser().getUsername().compareTo(user.getUsername()) == 0) {
                c.getLoginthread().sendMes(1000);
                c.getLoginthread().sendMes(changeUser(user));
            }
        }
//
//        inout.writeObject(1000);
//        User u = changeUser(user);
//        inout.writeObject(u);
    }

    private User changeUser(SVUser u) {
        User us = new User(u.getUsername(), u.getPassword(), u.getImage(), u.getStatus(), u.getOnline(),
                u.getIdPerson(), u.getName(), u.getAddress(), u.getDob(), u.getSdt());
        us.setListFriend(u.getListFriend());
        us.setListRoom(u.getListRoom());
        return us;
    }

    private Room changeRoom(SVRoom r) {
        return new Room(r.getIdRoom(), r.getName(), r.getListUser(), r.getListMess());
    }

    private Message changeMess(SVMess m) {
        return new Message(m.getUser(), m.getRoom(), m.getMess(), m.getTime());
    }

    public void close() {
        inout.writeObject(2010);
        inout.exit();
        run = false;
    }

    private void sendToFriend(SVUser user) {
        Iterator<Connect> listC = server.getListConnect().iterator();
        user.connect();
        user.getFriendFromDB();
        user.closeConnect();
        ArrayList<User> listU = user.getListFriend();
        System.out.println("list friend send to friend: " + listU.size());
        System.out.println("list connect : " + server.getListConnect().size());
        while (listC.hasNext()) {
            Connect c = listC.next();
            System.out.println("user name connect:" + c.getUser().getUsername());
            for (User u : listU) {
                System.out.println("user name: " + u.getUsername());
                if (c.getUser().getUsername().compareTo(u.getUsername()) == 0) {
                    //nhận 203 để in ra tin nhắn cũ
                    System.out.println("send 1 friend");
                    c.getLoginthread().sendOnl();
                }
            }
        }
    }

    private void createNewConnect() {
        inout.writeObject(9000);
        try {
            ServerSocket newsv = new ServerSocket(2311);
            Socket newCl = newsv.accept();
            InputStream in = newCl.getInputStream();
            imagePath = "Image//" + user.getUsername() + ".jpg";
            FileOutputStream out = new FileOutputStream(imagePath);
            byte[] buff = new byte[1024];
            int count;
            while ((count = in.read(buff)) >= 0) {
                out.write(buff, 0, count);
            }
            out.close();
            in.close();
            newCl.close();
            newsv.close();
        } catch (IOException ex) {
        }
    }

    synchronized private void sendImage() {
        user.connect();
        User u = user.getUserInfo();
        user.closeConnect();
        System.out.println("ip khach: "+inout.getLocation());
        try {
            wait(100);
            File imageFile = new File(u.getImage());
            Socket newcl = new Socket(inout.getLocation(), 2378);
            OutputStream out = newcl.getOutputStream();
            FileInputStream in = new FileInputStream(imageFile);
            byte[] buff = new byte[1024];
            int count;
            while ((count = in.read(buff)) >= 0) {
                out.write(buff, 0, count);
            }
            out.close();
            in.close();
            newcl.close();
        } catch (IOException | InterruptedException ex) {
        }
//        }

    }

    private void sendStatus() {
        user.connect();
        User u = user.getUserInfo();
        user.closeConnect();
        if (u.getStatus() != null && !u.getStatus().equals("")) {
            inout.writeObject(109);
            inout.writeObject(u);
        }
    }

    synchronized private void sendInit() {
        try {
            sendListOnl(this.user);
            wait(50);
            sendToFriend(this.user);
            sendStatus();
            wait(50);
            sendRequestImage();
        } catch (InterruptedException ex) {
        }
    }

    private void sendRequestImage() {
        user.connect();
        User u = user.getUserInfo();
        user.closeConnect();
        if (u.getImage() != null && !u.getImage().equals(" ") && !u.getImage().equals("")) {
            inout.writeObject(9111);
        }
    }

}
