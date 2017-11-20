/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Model;

import Model.Message;
import Server.DAO.MessDAO;
import java.io.Serializable;

/**
 *
 * @author HuyVan
 */
public class SVMess extends Message implements Serializable{
    private MessDAO messdao;

    public SVMess() {
        super();
    }
    public SVMess(Message m){
        this.idmess = m.getIdmess();
        this.mess = m.getMess();
        this.room = m.getRoom();
        this.time = m.getTime();
        this.user = m.getUser();
    }
    public void addMesInDB(){
        this.messdao.addObject(this);
    }
    
    public void close(){
        this.messdao.close();
    }
    
    public void connectDB(){
        this.messdao = new MessDAO();
    }
}
