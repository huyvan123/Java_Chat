/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Control;

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

    public InOutData() {
    }

    public InOutData(Socket socket) {
        try {
            this.client = socket;
            this.ous = new ObjectOutputStream(client.getOutputStream());
            this.ois = new ObjectInputStream(client.getInputStream());

        } catch (IOException e) {
        }
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        Object o = ois.readObject();
        return o;
    }

    public void writeObject(Object object) {
        try {
            ous.writeObject(object);

        } catch (IOException e) {
        }
    }

    public void exit() {
        try {

            this.ois.close();
            this.ous.close();
            this.client.close();
        } catch (IOException e) {
        }
    }

    public ObjectInputStream getOis() {
        return ois;
    }

}
