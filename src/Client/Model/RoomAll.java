/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Model;

import Client.Control.ChatAll;
import Model.Room;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author HuyVan
 */
public class RoomAll {

    private Room room;
    private ChatAll chatAll;

    public RoomAll() {
    }

    public RoomAll(Room room, ChatAll chatAll) {
        this.room = room;
        this.chatAll = chatAll;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ChatAll getChatAll() {
        return chatAll;
    }

    public void setChatAll(ChatAll chatAll) {
        this.chatAll = chatAll;
    }

}
