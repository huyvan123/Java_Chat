/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Model;

import Model.Room;
import Server.DAO.RoomDAO;
import java.io.Serializable;

/**
 *
 * @author HuyVan
 */
public class SVRoom extends Room implements Serializable{

    private RoomDAO roomdao;

    public SVRoom() {
        super();
    }
    
    public SVRoom(Room r){
        this.idRoom = r.getIdRoom();
        this.listMess = r.getListMess();
        this.listUser = r.getListUser();
        this.name = r.getName();
    }

    public void addRoomInDB() {
        this.roomdao.addObject(this);
    }

    public void getListMessFromDB() {
        this.listMess = this.roomdao.getListMess(this.idRoom);
    }

    public void close() {
        this.roomdao.close();
    }

    public int getLastIdRoom() {
        return this.roomdao.getLastIdRoom();
    }
    
    public void connectDB(){
        this.roomdao = new RoomDAO();
    }
    
}
