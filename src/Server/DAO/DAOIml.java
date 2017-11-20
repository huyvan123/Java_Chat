/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.DAO;

/**
 *
 * @author HuyVan
 * @param <T>
 */
public interface DAOIml<T> {

    void addObject(T t);

    boolean checkExist(T t);

    void update(T t);

    void close();
}
