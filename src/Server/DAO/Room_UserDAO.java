/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.DAO;

import Server.Model.Room_User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author HuyVan
 */
public class Room_UserDAO implements DAOIml<Room_User>{
    private final Connection connect = new DAOConnect().getConnect();

    public Room_UserDAO() {
        super();
    }

    @Override
    public void addObject(Room_User t) {
        String sql = "insert into user.room_user(room_user.idroom,room_user.user1, room_user.user2)" +
                        " value (?,?,?)";
        try {
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setInt(1, t.getRoom().getIdRoom());
            pre.setString(2, t.getUser1().getUsername());
            pre.setString(3, t.getUser2().getUsername());
            pre.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    @Override
    public boolean checkExist(Room_User t) {
        return false;
    }

    @Override
    public void close() {
        try {
            this.connect.close();
        } catch (SQLException ex) {
        }
    }
    
    public Integer getIdRoomChat(Room_User t){
        String sql = "select room_user.idroom from user.room_user" +
                    "   where (room_user.user1 =? and room_user.user2=?)" +
                    "   or (room_user.user1= ? and room_user.user2 = ?)";
        try {
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setString(1, t.getUser1().getUsername());
            pre.setString(2, t.getUser2().getUsername());
            pre.setString(3, t.getUser2().getUsername());
            pre.setString(4, t.getUser1().getUsername());
            ResultSet rs = pre.executeQuery();
            while(rs.next())return rs.getInt(1);
        } catch (SQLException ex) {
        }
        return null;
    }

    @Override
    public void update(Room_User t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
