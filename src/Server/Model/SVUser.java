/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Model;

import Model.User;
import Server.DAO.UserDAO;
import java.io.Serializable;

/**
 *
 * @author HuyVan
 */
public class SVUser extends User implements Serializable{

    private UserDAO userdao;

    public SVUser() {
        super();
    }
    
    public SVUser(User user){
        this.image = user.getImage();
        this.listFriend = user.getListFriend();
        this.listRoom = user.getListRoom();
        this.online = user.getOnline();
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.status = user.getStatus();
        this.idPerson = user.getIdPerson();
        this.name = user.getName();
        this.dob = user.getDob();
        this.address = user.getAddress();
        this.sdt = user.getSdt();
    }

    public void addUserIntoDB() {
        this.userdao.addObject(this);
    }

    public void getFriendFromDB() {
        this.listFriend = this.userdao.getFriend(this);
    }

    public boolean checkLoginUser() {
        return this.userdao.checkLoginUser(this);
    }

    public boolean checkRegUser() {
        return this.userdao.checkRegUser(this);
    }

    public User getUserInfo() {
        return this.userdao.getUserInfo(this.username);
    }

    public void updateImage(String local) {
        this.userdao.updateImage(this, local);
    }

    public void closeConnect() {
        this.userdao.close();
    }

    public void connect() {
        this.userdao = new UserDAO();
    }
    
    public void update(){
        this.userdao.update(this);
    }
    
    public void updatePW(String pass){
        this.userdao.updatePW(this, pass);
    }
}
