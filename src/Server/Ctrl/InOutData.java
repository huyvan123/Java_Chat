/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Ctrl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author HuyVan
 */
public class InOutData {

    private ObjectInputStream ois;
    private ObjectOutputStream ous;
    private Socket client;
    private String location;

    public InOutData() {
    }

    public InOutData(Socket socket) {
        this.client = socket;
        this.location = socket.getInetAddress().getHostAddress();
        try {
            this.ois = new ObjectInputStream(client.getInputStream());
            this.ous = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException ex) {
        }
    }

    public Object readObject() {
        Object o = null;
        try {
            o = ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
        }
        return o;
    }

    public void writeObject(Object object) {
        try {
            ous.writeObject(object);
        } catch (IOException ex) {
        }
    }

    public void exit() {
        try {
            this.ois.close();
            this.ous.close();
            this.client.close();
        } catch (IOException ex) {
        }
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    public String getLocation() {
        return location;
    }

}
