/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Model;

import Server.Ctrl.LoginThread;

/**
 *
 * @author HuyVan
 */
public class Connect {
    public static int idth=100;
    private int idthread;
    private LoginThread loginthread;
    private SVUser user;

    public Connect() {
        this.idthread=++idth;
    }

    public Connect(LoginThread loginthread, SVUser user) {
        this.idthread = ++idth;
        this.loginthread = loginthread;
        this.user = user;
    }

    public int getIdthread() {
        return idthread;
    }

    public void setIdthread(int idthread) {
        this.idthread = idthread;
    }

    public LoginThread getLoginthread() {
        return loginthread;
    }

    public void setLoginthread(LoginThread loginthread) {
        this.loginthread = loginthread;
    }

    public SVUser getUser() {
        return user;
    }

    public void setUser(SVUser user) {
        this.user = user;
    }

}
