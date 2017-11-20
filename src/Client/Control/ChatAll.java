/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Control;

import Client.View.ChatRieng;
import Client.View.MainView;
import Client.View.ViewChatAll;
import Client.Model.RoomCr;
import Client.View.MemberOption;
import Model.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author HuyVan
 */
public class ChatAll extends Thread {

    private final InOutData inout;
    private final ViewChatAll view;
    private final User user;
    private User u2;
    private boolean run = true;
    private final MainView mv;
    private ChatRieng chatRieng;
    private ChatRiengThread crt;
    private AddFriendThread addFriendThread;
    private MemberOption memview;

    private ArrayList<RoomCr> listRoomPr = new ArrayList<>();
    private int check;
    private String mess;
    private final Main main;

    public ChatAll(InOutData inout, ViewChatAll view, User user, MainView m, Main ma, ArrayList<RoomCr> l) {
        this.inout = inout;
        this.view = view;
        this.user = user;
        this.mv = m;
        this.check = 0;
        this.mess = "";
        this.main = ma;
        this.listRoomPr = l;
        view.addChatAllListener(new chatAllListener());
        view.addExitChatAllListener(new chatAllListener());
        this.start();
    }

    @Override
    synchronized public void run() {
        while (run) {
            try {
                int request = this.getCheck();
                System.out.println("check all: " + request);
                switch (request) {
                    case 200:
                        //in ra bang chat chung
                        view.addMess(this.getMessAll());
                        System.out.println(this.getMessAll());
                        setCheck(0);
                        this.wait();
                        break;
                    case 202:
                        //in danh sach in room (bao gom trang thai on = 1, off=0)
                        String s = this.getMessAll();
                        ArrayList<JButton> listB = new ArrayList();
                        ArrayList<JButton> listB1 = new ArrayList();
                        StringTokenizer toc = new StringTokenizer(s, ",");
                        int y = -24;
                        while (toc.hasMoreTokens()) {
                            String t = toc.nextToken();
                            JButton b = new JButton(t);
                            JButton b1 = new JButton("View Info");
                            b1.setName(t);
                            b.setSize(70, 23);
                            b1.setSize(100, 23);
                            y += 24;
                            b.setLocation(0, y);
                            b1.setLocation(71, y);
                            if (user.getUsername().equals(t)) {
                                b.setEnabled(false);
                                b1.setEnabled(false);
                            } else {
                                b.addActionListener(new ChatRiengListener());
                                b1.addActionListener(new ChatRiengListener());
                            }
                            listB1.add(b1);
                            listB.add(b);
                        }
                        view.remove();
                        view.addMember(listB);
                        view.addMember(listB1);
                        System.out.println("ra 202 - chatall");
                        this.setCheck(0);
                        this.wait();
                        break;
                    case 1090:

                            memview = new MemberOption();
                            addFriendThread = new AddFriendThread(memview, user, inout, u2);
                            memview.setVisible(true);
                        
                        setCheck(0);
                        wait();
                        break;
                    default:
                        this.wait();
                        break;
                }
            } catch (NumberFormatException | InterruptedException e) {
            }
        }

    }

    private class ChatRiengListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("View Info")) {
                JButton b = (JButton) e.getSource();
                String username = b.getName();
                User user1 = new User();
                user1.setUsername(username);
                System.out.println("user name muon xem info: " + username);
                inout.writeObject("1030");
                inout.writeObject(user1);

            } else {
                if (JOptionPane.showConfirmDialog(null, "Bạn có muốn chat với người này?", "Xac nhan chat rieng", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    String username = e.getActionCommand();
                    User user1 = new User();
                    user1.setUsername(username);
                    if (checkPrivateChat(user1)) {
                        mv.showMess("Dang chat voi nguoi nay!");
                        main.getPrivateChatRiengThread(user1).getView().setVisible(true);
                    } else {
                        chatRieng = new ChatRieng();
                        chatRieng.setTitle(username);
                        main.setViewPrivate(chatRieng);
                        inout.writeObject("103");
                        inout.writeObject(user1);
                    }
                } else {
                }
            }
        }
    }

    class chatAllListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().compareTo("Exit") == 0) {
                mv.enableButton();
                view.setVisible(false);
            } else {
                String mess = view.getMess();
                view.addMess("Me: " + mess + "\n");
                inout.writeObject("101");
                inout.writeObject(user.getUsername() + ": " + mess + "\n");
            }
        }
    }

    public void close() {
        this.mv.showMess("Co nguoi khac dang nhap!");
        this.chatRieng.dispose();
        this.crt.destroy();
        this.view.dispose();
        this.run = false;
        this.mv.dispose();
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

    synchronized public void sTart() {
        this.notifyAll();
    }

    public void setCheck(int c) {
        this.check = c;
    }

    public void thoat() {
        if (memview != null) {
            memview.dispose();
        }
        if (addFriendThread != null) {
            addFriendThread.interrupt();
        }
        this.stop();
    }

    public int getCheck() {
        return this.check;
    }

    public void setMessAll(String m) {
        this.mess = m;
    }

    public String getMessAll() {
        return this.mess;
    }

    private boolean checkPrivateChat(User user) {
        if (this.listRoomPr == null) {
            return false;
        } else {
            for (RoomCr rom : listRoomPr) {
                if (rom.getRoom().checkUserInList(user)) {
                    return true;
                }
            }
        }
        return false;
    }

    public User getU2() {
        return u2;
    }

    public void setU2(User u2) {
        this.u2 = u2;
    }

}
