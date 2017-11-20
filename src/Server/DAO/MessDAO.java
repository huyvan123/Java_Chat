/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.DAO;

import Model.Message;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author HuyVan
 */
public class MessDAO implements  DAOIml<Message>{
    private final Connection connect;

    public MessDAO() {
        this.connect = new DAOConnect().getConnect();
    }
    
    

    @Override
    public void addObject(Message mess) {

        try {
            String sql = "INSERT INTO user.messenger(messenger.idmessenger,messenger.iduser,messenger.idroom,messenger.time,messenger.message)"
                    + " VALUE (?, ?, ?, ?, ?)";
            PreparedStatement pre  = connect.prepareStatement(sql);
            pre.setInt(1, mess.getIdmess());
            pre.setString(2, mess.getUser().getUsername());
            pre.setInt(3, mess.getRoom().getIdRoom());
            pre.setString(4, mess.getTime());
            pre.setString(5, mess.getMess());
            pre.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    @Override
    public boolean checkExist(Message t) {
        return false;
    }

    @Override
    public void close() {
        try {
            this.connect.close();
        } catch (SQLException ex) {
        }
    }

    @Override
    public void update(Message t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
