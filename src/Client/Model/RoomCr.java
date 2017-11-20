/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Model;

import Client.Control.ChatRiengThread;
import Model.Room;

/**
 *
 * @author HuyVan
 */
public class RoomCr {
    public static int id=0;
    private Room room;
    private ChatRiengThread chatrieng;

    public RoomCr() {
    }

    public RoomCr(Room idroom, ChatRiengThread chatrieng) {
        this.room = idroom;
        this.chatrieng = chatrieng;
        id++;
    }

    public int getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ChatRiengThread getChatrieng() {
        return chatrieng;
    }

    public void setChatrieng(ChatRiengThread chatrieng) {
        this.chatrieng = chatrieng;
    }
    
}
