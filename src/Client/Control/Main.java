/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Control;

import Client.View.ChatRieng;
import Client.View.MainView;
import Client.View.ViewChatAll;
import Client.View.ViewLogin;
import Client.Model.RoomAll;
import Client.Model.RoomCr;
import Client.View.UserInfo;
import Model.Message;
import Model.Room;
import Model.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author HuyVan
 */
public class Main extends Thread implements ActionListener {

    private final InOutData inout;
    public ChatRieng viewPrivate;
    private UserInfo userinfoView;
    private ViewChatAll viewAll;
    private final MainView mainView;

    private ChatRiengThread privateChat;
    private ChatAll chatAll;
    private ViewDetailThread detailThread;

    private File imageFile;
    private User user;
    private Room room;
    private boolean run = true;
    private final ArrayList<RoomCr> listRoomPr = new ArrayList<>();
    private ArrayList<RoomAll> listRoomAll = new ArrayList<>();
    private final ArrayList<Integer> listp = new ArrayList<>();
    private int check = 0;

    public Main(InOutData inout, MainView view) {
        this.inout = inout;
        this.mainView = view;
        this.mainView.getChatAllButton().addActionListener(this);
        this.mainView.addChoiseClicked(new choiseClicker());
        this.mainView.addFileChooserAction(new FileChooserListener());
        chay();
    }

    private void chay() {
        this.start();
    }

    @Override
    synchronized public void run() {
        while (run) {
            try {

                int request = (Integer) inout.readObject();
                System.out.println("request: " + request);
                switch (request) {
                    case 109:
                        user = (User) inout.readObject();
                        //System.out.println("status : "+user.getStatus());
                        mainView.setStatus(user.getStatus());
                        break;
                    case 200:
                        //in ra bang chat chung
                        int room1 = (Integer) inout.readObject();
                        String mess1 = (String) inout.readObject();
                        chatAll.setCheck(200);
                        chatAll.setMessAll(mess1);
                        chatAll.sTart();
                        break;
                    case 202:
                        //in danh sach friend (bao gom trang thai on = 1, off=0)
                        String mess2 = (String) inout.readObject();
                        System.out.println("mess 202:" + mess2);
                        listRoomAll.get(0).getChatAll().setCheck(202);
                        listRoomAll.get(0).getChatAll().setMessAll(mess2);
                        System.out.println("listroom size: " + listRoomAll.size());
                        System.out.println("user in room: " + mess2);
                        System.out.println("ra 202  ");
                        chatAll.sTart();
                        break;
                    case 204:
                        //nhan yeu cau chat rieng
                        room = (Room) inout.readObject();
                        User user1 = (User) inout.readObject();
                        ChatRieng cr = new ChatRieng();
                        cr.setTitle(user1.getUsername());
                        listRoomPr.add(new RoomCr(room, new ChatRiengThread(inout, user, cr, room)));
                        cr.setVisible(true);
                        if (room.getListMess() != null) {
                            for (Message m : room.getListMess()) {
                                cr.addDate(m.getTime() + "\n");
                                cr.addMess(fukMess(m.getMess()) + "\n");
                            }
                        }

                        break;
                    case 203:
                        //nhan lai room de tham gia chat rieng
                        room = (Room) inout.readObject();
                        privateChat = new ChatRiengThread(inout, user, viewPrivate, room);
                        listRoomPr.add(new RoomCr(room, privateChat));
                        viewPrivate.setVisible(true);
                        if (room.getListMess() != null) {
                            for (Message m : room.getListMess()) {
                                viewPrivate.addDate(m.getTime() + "\n");
                                viewPrivate.addMess(fukMess(m.getMess()) + "\n");
                            }
                        }

                        break;
                    case 201:
                        //hien thi lich su chat rieng
                        room = (Room) inout.readObject();
                        Message messa = (Message) inout.readObject();
                        for (RoomCr r : this.listRoomPr) {
                            if (r.getRoom().getIdRoom() == room.getIdRoom()) {
                                r.getChatrieng().setCheck(100);
                                r.getChatrieng().setMess(messa);
                                r.getChatrieng().sTart();
                                break;
                            }
                        }
                        break;
                    case 2010:
                        this.close();
                        break;
                    case 1000:
                        setFriend();
                        break;

                    case 1010:
                        ArrayList<Room> listR = (ArrayList<Room>) inout.readObject();
                        if (!listRoomPr.isEmpty()) {
                            for (RoomCr r : listRoomPr) {
                                if (checkRoomExist(r.getRoom(), listR)) {
                                    for (Room rom : listR) {
                                        if (rom.getIdRoom() == r.getRoom().getIdRoom()) {
                                            int index = listRoomPr.indexOf(r);
                                            listRoomPr.remove(index);
                                            r.getChatrieng().getView().dispose();
                                            r.getChatrieng().interrupt();
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;

                    case 2202:
                        //xem thong tin ca nhan
                        user = (User) inout.readObject();
                        if (detailThread == null) {
                            this.userinfoView = new UserInfo();
                            detailThread = new ViewDetailThread(userinfoView, user, inout);
                            userinfoView.setVisible(true);
                        } else {
                            detailThread.setUser(user);
                            detailThread.setCheck(100);
                            detailThread.sTart();
                            userinfoView.setVisible(true);
                        }
                        break;
                    case 1211:
                        detailThread.setCheck(102);
                        detailThread.sTart();
                        break;
                    case 1212:
                        detailThread.setCheck(101);
                        detailThread.sTart();
                        break;
                    case 1090:
                        User user2 = (User) inout.readObject();
                        chatAll.setCheck(1090);
                        chatAll.setU2(user2);
                        chatAll.sTart();
                        break;
                    case 2017:
                        User uradd = (User) inout.readObject();
                        if (JOptionPane.showConfirmDialog(null, uradd.getUsername() + " đã gửi yêu cầu kết bạn,\n bạn có chấp nhận hi sinh cả đời cho người mình yêu ko?", "Xác nhận yêu cầu kết bạn!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            inout.writeObject("2018");
                            inout.writeObject(uradd);
                        } else {
                            inout.writeObject("2019");
                            inout.writeObject(uradd);
                        }
                        break;
                    case 2020:
                        User uraddc1 = (User) inout.readObject();
                        JOptionPane.showMessageDialog(viewAll, uraddc1.getUsername() + " accepted your friend request!");
                        inout.writeObject("2121");
                        inout.writeObject(uraddc1);
                        break;
                    case 2021:
                        User uraddc2 = (User) inout.readObject();
                        JOptionPane.showMessageDialog(viewAll, uraddc2.getUsername() + " doesn'n accept your friend request!");
                        break;
                    case 1112:
                        JOptionPane.showMessageDialog(mainView, "Delete successful!");
                        break;
                    case 9000: {
                        try {
                            wait(100);
                        } catch (InterruptedException ex) {
                        }
                    }
                    creatNewClient();
                    break;
                    case 9111:
                        setImage();
                        break;
                    default:
                        break;
                }
            } catch (NumberFormatException | IOException | ClassNotFoundException e) {
            }
        }
    }

    private void setFriend() {
        try {
            user = (User) inout.readObject();
            System.out.println(user == null);
        } catch (IOException | ClassNotFoundException ex) {
        }
        if (!user.getListFriend().isEmpty()) {
            System.out.println("list fiend size: " + user.getListFriend().size());
            System.out.println("online status: " + user.getListFriend().get(0).getOnline());
            String s = "";
            ArrayList<JButton> listB = new ArrayList();
            ArrayList<JButton> listB1 = new ArrayList();
            try {
                int y = -24;
                for (User u : user.getListFriend()) {
                    String name = u.getUsername();
                    JButton b = new JButton(name);
                    JButton b1 = new JButton("x");
                    b1.setName(name);
                    b.setSize(70, 23);
                    b1.setSize(50, 23);
                    y += 24;
                    b.setLocation(0, y);
                    b1.setLocation(71, y);
                    if (u.getOnline() == 1) {
                        b.addActionListener(this);
                    } else {
                        b.setEnabled(false);
                    }
                    b1.addActionListener(this);
                    listB.add(b);
                    listB1.add(b1);
                }
            } catch (Exception e) {
            }
            this.mainView.removeAllPanel1();
            this.mainView.setFriend(listB);
            this.mainView.setFriend(listB1);
        } else {
            this.mainView.setNullFriend();
        }
    }

    public void close() {
        ArrayList<Room> listR = new ArrayList<>();
        if (this.listRoomPr != null) {
            for (RoomCr r : this.listRoomPr) {
                listR.add(r.getRoom());
                r.getChatrieng().getView().dispose();
                r.getChatrieng().interrupt();
            }
        }
        if (this.getCheck() == 100) {

        } else {
            JOptionPane.showMessageDialog(null, "Co nguoi dang nhap!");
        }
        if (chatAll != null) {
            chatAll.thoat();
            this.chatAll.interrupt();
        }
        if (detailThread != null) {
            this.detailThread.interrupt();
        }
        this.mainView.dispose();
        if (privateChat != null) {
            this.privateChat.getView().dispose();
            this.privateChat.interrupt();
        }
        this.run = false;
        if (viewAll != null) {
            this.viewAll.dispose();
        }
        if (userinfoView != null) {
            this.userinfoView.dispose();
        }
        if (viewPrivate != null) {
            this.viewPrivate.dispose();

        }
        inout.writeObject("102");
        inout.writeObject(listR);
        this.inout.exit();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Chat All")) {
            if (checkP(1)) {
                viewAll.setVisible(true);
            } else {
                try {
                    listp.add(1);
                    viewAll = new ViewChatAll();
                    chatAll = new ChatAll(inout, viewAll, user, mainView, this, this.listRoomPr);
                    //cho(100);
                    mainView.disableButton();
                    viewAll.setVisible(true);
                    Room r = new Room();
                    r.setIdRoom(1);
                    listRoomAll.add(new RoomAll(r, chatAll));
                    inout.writeObject("100");
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (e.getActionCommand().equals("x")) {
            JButton b = (JButton) e.getSource();
            String username = b.getName();
            User user1 = new User();
            user1.setUsername(username);
            inout.writeObject("1111");
            inout.writeObject(user1);

        } else {
            if (JOptionPane.showConfirmDialog(null, "Bạn có muốn chat với người này?", "Xac nhan chat rieng", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String username = e.getActionCommand();
                User user1 = new User();
                user1.setUsername(username);
                if (checkPrivateChat(user1)) {
                    JOptionPane.showMessageDialog(null, "Dang chat voi nguoi nay!");
                    getPrivateChatRiengThread(user1).getView().setVisible(true);
                } else {
                    viewPrivate = new ChatRieng();
                    viewPrivate.setTitle(username);
                    inout.writeObject("103");
                    inout.writeObject(user1);
                }
            } else {
            }
        }
    }

    synchronized void cho(int l) {
        try {
            wait(l);
        } catch (InterruptedException ex) {
        }
    }

    public boolean checkP(int idp) {
        if (listp.isEmpty()) {
            return false;
        } else {
            for (Integer i : listp) {
                if (i == idp) {
                    return true;
                }
            }
        }
        return false;
    }

    private String fukMess(String s) {
        String mes;
        StringTokenizer toc = new StringTokenizer(s, ":");
        String name = toc.nextToken(":");
        if (name.equals(user.getUsername())) {
            StringBuilder ssd = new StringBuilder(s);
            ssd.replace(0, name.length(), "Me");
            mes = ssd.toString();
        } else {
            mes = s;
        }
        return mes;
    }

    public void set(ArrayList<RoomAll> list) {
        this.listRoomAll = list;
    }

    synchronized public void sTart() {
        this.notifyAll();
    }

    public void setCheck(int c) {
        this.check = c;
    }

    public int getCheck() {
        return this.check;
    }

    public void setViewPrivate(ChatRieng c) {
        this.viewPrivate = c;
    }

    public boolean checkRoomExist(Room r, ArrayList<Room> list) {
        for (Room roomr : list) {
            if (roomr.getIdRoom() == r.getIdRoom()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkPrivateChat(User user) {
        if (this.listRoomPr == null) {
            return false;
        } else {
            System.out.println("list room: " + listRoomPr.size());
            for (RoomCr rom : listRoomPr) {
                if (rom.getRoom().checkUserInList(user)) {
                    return true;
                }
            }
        }
        return false;
    }

    public ChatRiengThread getPrivateChatRiengThread(User user) {
        for (RoomCr rom : listRoomPr) {
            if (rom.getRoom().checkUserInList(user)) {
                return rom.getChatrieng();
            }
        }
        return null;
    }

    private void creatNewClient() {
        try {
            if (imageFile.exists()) {
                Socket newcl = new Socket("localhost", 2311);
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
            } else {
                System.out.println("Khong ton tai file!");
            }
        } catch (IOException ex) {
        }
    }

    private void setImage() {
        String imagePath = "ImageUser//" + this.user.getUsername() + ".jpg";
        inout.writeObject("9222");
        try {
            ServerSocket newsv = new ServerSocket(2378);
            Socket newcl = newsv.accept();
            InputStream in = newcl.getInputStream();
            FileOutputStream out = new FileOutputStream(imagePath);
            byte[] buff = new byte[1024];
            int count;
            while ((count = in.read(buff)) >= 0) {
                out.write(buff, 0, count);
            }
            out.close();
            in.close();
            newcl.close();
            newsv.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageIcon ima = new ImageIcon(imagePath);
        mainView.setImage(ima);
    }

    private class FileChooserListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
            }
            JFileChooser fileChooser = new JFileChooser();
            FileFilter filter = new FileNameExtensionFilter("Image File", "jpg", "png");
            fileChooser.setFileFilter(filter);
            fileChooser.setMultiSelectionEnabled(false);
            int action = fileChooser.showOpenDialog(mainView);
            if (action == JFileChooser.APPROVE_OPTION) {
                imageFile = fileChooser.getSelectedFile();
                ImageIcon image = new ImageIcon(imageFile.getAbsolutePath());
                mainView.setImage(image);
                inout.writeObject("9999");
            } else {

            }
        }
    }

    private class choiseClicker implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (mainView.getjComboBox1().getSelectedItem().toString().equals("Log Out")) {
                if (JOptionPane.showConfirmDialog(null, "Ban co muon thoat?", "Conirm Logout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    setCheck(100);
                    close();
                    ViewLogin view = new ViewLogin();
                    Login log = new Login(view);
                    view.setVisible(true);
                }
            } else if (mainView.getjComboBox1().getSelectedItem().toString().equals("View Info")) {
                inout.writeObject("1102");
            }
        }
    }
}
