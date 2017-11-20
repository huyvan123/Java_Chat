/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Model;

import Model.Room;
import Model.User;
import Server.DAO.Room_UserDAO;

/**
 *
 * @author HuyVan
 */
public class Room_User {
    private int id;
    private SVRoom room;
    private SVUser user1;
    private SVUser user2;
    private Room_UserDAO rudao;

    public Room_User() {
    }

    public Room_User(SVRoom room, SVUser user1, SVUser user2) {
        this.room = room;
        this.user1 = user1;
        this.user2 = user2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(SVRoom room) {
        this.room = room;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(SVUser user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(SVUser user2) {
        this.user2 = user2;
    }
    
    public void addRoom_User(){
        this.rudao.addObject(this);
    }
    public Integer getIdRoomChat(){
        return this.rudao.getIdRoomChat(this);
    }
    
    public void close(){
        this.rudao.close();
    }
    
    public void connectDB(){
        this.rudao = new Room_UserDAO();
    }
}
