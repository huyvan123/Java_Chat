/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Model;

import Model.Friend;
import Server.DAO.FriendDAO;

/**
 *
 * @author HuyVan
 */
public class SVFriend extends Friend{
    private final FriendDAO fdao = new FriendDAO();

    public SVFriend() {
        super();
    }
    
    public SVFriend(Friend f){
        this.idFriend = f.getIdFriend();
        this.user1 = f.getUser1();
        this.user2 = f.getUser2();
    }
    public void addFriend(){
       this.fdao.addObject(this);
    }
    public void close(){
        this.fdao.close();
    }
    
    public void connect(){
        this.fdao.connect();
    }
    
    public void deleteDB(){
        this.fdao.detete(this);
    }
    
    public boolean checkExist(){
        return this.fdao.checkExist(this);
    }
    
}
