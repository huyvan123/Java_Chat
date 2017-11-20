/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.DAO;

import Model.Friend;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HuyVan
 */
public class FriendDAO implements DAOIml<Friend> {

    private Connection connect ;

    public FriendDAO() {
        super();
    }

    @Override
    public void addObject(Friend t) {

        try {
            String sql = "INSERT INTO user.friend(friend.user1, friend.user2)"
                    + " VALUE (? ,?)";
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setString(1, t.getUser1().getUsername());
            pre.setString(2, t.getUser2().getUsername());
            pre.executeUpdate();
        } catch (SQLException ex) {
        }

    }
    
    public void detete(Friend f){
        String sql = "delete from user.friend" +
                    " where (friend.user1 = ? and friend.user2 = ?)" +
                    " or (friend.user2 = ? and friend.user1 = ?)";
        try {
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setString(1, f.getUser1().getUsername());
            pre.setString(2, f.getUser2().getUsername());
            pre.setString(3, f.getUser1().getUsername());
            pre.setString(4, f.getUser2().getUsername());
            pre.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    @Override
    public boolean checkExist(Friend t) {
        String sql = "select * from user.frienD" +
                    "     where (friend.user1 = ? and friend.user2 = ?)" +
                    " or (friend.user2 = ? and friend.user1 = ?)";
        try {
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setString(1, t.getUser1().getUsername());
            pre.setString(2, t.getUser2().getUsername());
            pre.setString(3, t.getUser1().getUsername());
            pre.setString(4, t.getUser2().getUsername());
            ResultSet res = pre.executeQuery();
            while(res.next())return true;
        } catch (SQLException ex) {
        }
        return false;
    }

    @Override
    public void close() {
        try {
            this.connect.close();
        } catch (SQLException ex) {
        }
    }
    
    public void connect(){
        this.connect = new DAOConnect().getConnect();
    }

    @Override
    public void update(Friend t) {
    }

}
