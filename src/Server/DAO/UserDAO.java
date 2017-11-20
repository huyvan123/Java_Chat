/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.DAO;

import Model.Person;
import Model.User;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author HuyVan
 */
public class UserDAO implements DAOIml<User> {

    private final Connection connect;

    public UserDAO() {
        super();
        connect = new DAOConnect().getConnect();
    }

    @Override
    public void addObject(User user) {
        try {
            String sqlp = "insert into user.person(person.name,person.dateofbirth,person.address,person.phonenumber)"
                    + "value (?,?,?,?)";
            PreparedStatement pre = connect.prepareStatement(sqlp);
            pre.setString(1, user.getName());
            pre.setDate(2, user.getDob());
            pre.setString(3, user.getAddress());
            pre.setString(4, user.getSdt());
            pre.executeUpdate();
            int idp = this.getLastIDPerson();
            String sql = "INSERT INTO user.user(user.username,user.password,user.imagelocal,user.status,user.onl,user.idperson)"
                    + " VALUE (?, ?, ?, ?, ?, ?)";
            pre = connect.prepareStatement(sql);
            pre.setString(1, user.getUsername());
            pre.setString(2, user.getPassword());
            pre.setString(3, user.getImage());
            pre.setString(4, user.getStatus());
            pre.setInt(5, user.getOnline());
            pre.setInt(6, idp);
            pre.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    @Override
    public boolean checkExist(User t) {

        return false;
    }

    public ArrayList<User> getFriend(User user) {
        try {
            ArrayList<User> listUser = new ArrayList<>();
            String sql = "select *" +
                        " from user.user,user.person,(select *" +
                                                " from user.friend" +
                                                "  where user1 = ? or user2 = ?)as A" +
                        "   where ((user.username = A.user1 and user.username != ?)" +
                        "   or (user.username = A.user2 and user.username != ?))" +
                        "   and user.idperson = person.idperson";
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setString(1, user.getUsername());
            pre.setString(2, user.getUsername());
            pre.setString(3, user.getUsername());
            pre.setString(4, user.getUsername());
            ResultSet res = pre.executeQuery();
            while (res.next()) {
                String username = res.getString(1);
                String pass = res.getString(2);
                String imagelocal = res.getString(3);
                String status = res.getString(4);
                int onl = res.getInt(5);
                int idper = res.getInt(6);
                String name = res.getString(8);
                Date dob = res.getDate(9);
                String address = res.getString(10);
                String phone = res.getString(11);
                listUser.add(new User(username, pass, imagelocal, status, onl, idper, name, address, dob, phone));
            }
            return listUser;
        } catch (SQLException ex) {
        }
        return null;
    }

    public boolean checkLoginUser(User user) {
        try {

            String sql = "SELECT * "
                    + "    FROM user.user"
                    + "    where user.password = ? and user.username = ?";
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setString(1, user.getPassword());
            pre.setString(2, user.getUsername());
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
        }
        return false;
    }

    public boolean checkRegUser(User user) {
        try {
            String sql = "SELECT *"
                    + " FROM user.user"
                    + " WHERE user.username = ? ";
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setString(1, user.getUsername());
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                return true;
            }
        } catch (SQLException e) {

        }
        return false;
    }

    public User getUserInfo(String name) {
        try {
            System.out.println("user name: "+name);
            String sql = "SELECT *"
                    + "  FROM user.user, user.person"
                    + "   WHERE user.username = ? and person.idperson = user.idperson";
            PreparedStatement pre = connect.prepareCall(sql);
            pre.setString(1, name);
            ResultSet res = pre.executeQuery();
            while (res.next()) {
                String username = res.getString(1);
                String pass = res.getString(2);
                String imagelocal = res.getString(3);
                String status = res.getString(4);
                int onl = res.getInt(5);
                int idper = res.getInt(6);
                String nameu = res.getString(8);
                Date dob = res.getDate(9);
                String address = res.getString(10);
                String phone = res.getString(11);
                return new User(username, pass, imagelocal, status, onl, idper, nameu, address, dob, phone);
            }
        } catch (SQLException ex) {
        }
        return null;
    }

    public void updateImage(User user, String local) {
        try {
            String sql = "update user.user"
                    + " set user.imagelocal = ?"
                    + " where user.username = ?";
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setString(1, local);
            pre.setString(2, user.getUsername());
            pre.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    @Override
    public void close() {
        try {
            this.connect.close();
        } catch (SQLException ex) {
        }
    }

    @Override
    public void update(User user) {
        try {
            String sqlp = "update user.person"
                    + " set person.name = ?, person.dateofbirth =?, person.address = ?, person.phonenumber=?"
                    + " where person.idperson = ?";
            PreparedStatement pre = connect.prepareStatement(sqlp);
            pre.setString(1, user.getName());
            pre.setDate(2, user.getDob());
            pre.setString(3, user.getAddress());
            pre.setString(4, user.getSdt());
            pre.setInt(5, user.getIdPerson());
            pre.executeUpdate();
            
            String sqlu = "update user.user"
                    + " set user.status = ?"
                    + " where user.username = ?";
            PreparedStatement pre1  = connect.prepareStatement(sqlu);
            pre1.setString(1, user.getStatus());
            pre1.setString(2, user.getUsername());
            pre1.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    private int getLastIDPerson() {
        String sql = "select max(person.idperson)"
                + " from user.person";
        try {
            PreparedStatement pre = connect.prepareStatement(sql);
            ResultSet res = pre.executeQuery();
            while (res.next()) {
                return res.getInt(1);
            }
        } catch (SQLException ex) {
        }
        return 0;
    }

    public void updatePW(User u, String pass) {
        String sqlu = "update user.user"
                + " set user.password = ?"
                + " where user.username = ?";
        try {
            PreparedStatement pre = connect.prepareStatement(sqlu);
            pre.setString(1, pass);
            pre.setString(2, u.getUsername());
            pre.executeUpdate();
        } catch (SQLException ex) {
        }
    }
}
