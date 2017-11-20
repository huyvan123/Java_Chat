/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.DAO;

import Model.Message;
import Model.Room;
import Model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author HuyVan
 */
public class RoomDAO implements DAOIml<Room> {

    private final Connection connect = new DAOConnect().getConnect();

    public RoomDAO() {
        super();
    }

    @Override
    public void addObject(Room t) {
        String sql = "insert into user.room(room.name) value (?)";
        try {
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setString(1, t.getName());
            pre.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    @Override
    public boolean checkExist(Room t) {
        return false;
    }

    public ArrayList<Message> getListMess(int idroom) {
        try {
            ArrayList<Message> messlist = new ArrayList<>();
            String sql = "SELECT *"
                    + " FROM user.messenger"
                    + " WHERE messenger.idroom = ?";
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setInt(1, idroom);
            ResultSet res = pre.executeQuery();
            while (res.next()) {
                String iduser = res.getString(2);
                User u = new User();
                u.setUsername(iduser);
                Room r = new Room();
                r.setIdRoom(idroom);
                String time = res.getString(4);
                String message = res.getString(5);
                messlist.add(new Message(u, r, message, time));
            }
            Collections.sort(messlist, new Comparator<Message>() {

                @Override
                public int compare(Message o1, Message o2) {
                    if (o1.getTime().compareTo(o2.getTime()) > 0) {
                        return 1;
                    } else if (o1.getTime().compareTo(o2.getTime()) == 0) {
                        return 0;
                    }
                    return -1;
                }
            });
            return messlist;
        } catch (SQLException ex) {
        }
        return null;
    }

    @Override
    public void close() {
        try {
            this.connect.close();
        } catch (SQLException ex) {
        }
    }

    public int getLastIdRoom() {
        String sql = "select max(room.idroom) from user.room";
        try {
            PreparedStatement pre = connect.prepareStatement(sql);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
        }
        return 0;
    }

    @Override
    public void update(Room t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
