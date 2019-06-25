/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyAttendees;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author shane
 */
public class DBconnct {

    String DB_URL = "jdbc:mariadb://localhost:3306/calendar_app";
    Connection con = null;
    Statement sta = null;
    int numRows = 0;
    ResultSet rs = null;
    String error_msg = "";

    public DBconnct() {
        try {
            con = DriverManager.getConnection(DB_URL, "shane871112", "fhfh4646");
            //sta = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            sta = con.createStatement();
        } catch (SQLException ex) {
            System.out.println("資料庫操作出問題:" + ex.toString());
            ex.printStackTrace();
        }
    }

    public Statement getSta() {
        return sta;
    }

    public ArrayList<String> getGroupList() {

        ArrayList<String> group_list = new ArrayList<String>();

        String sql = String.format("SELECT *\n"
                + "FROM group_list");
        System.out.println(sql);

        try {
            rs = sta.executeQuery(sql);
            if (rs.next() == false) {
                System.out.println("查無資料");
            } else {

                do { //開始輸出
                    group_list.add(rs.getString("g_Name"));
                } while (rs.next());

                //System.out.println(msg);
            }
        } catch (SQLException ex) {
            System.out.println("資料庫操作出問題:" + ex.toString());
        }
        return group_list;
    }
    
     public ArrayList<String> getGroupList2() {

        ArrayList<String> group_list = new ArrayList<String>();

        String sql = String.format("SELECT *\n"
                + "FROM group_list");
        System.out.println(sql);

        try {
            rs = sta.executeQuery(sql);
            if (rs.next() == false) {
                System.out.println("查無資料");
            } else {

                do { //開始輸出
                    group_list.add(rs.getString("g_Id")+","+rs.getString("g_Name"));
                } while (rs.next());

                //System.out.println(msg);
            }
        } catch (SQLException ex) {
            System.out.println("資料庫操作出問題:" + ex.toString());
        }
        return group_list;
    }

    public ArrayList<String> getGroupUser(String name) {

        ArrayList<String> user_list = new ArrayList<String>();

        String sql = String.format("SELECT user_list.*\n"
                + "FROM group_list\n"
                + "INNER JOIN  advisor_usergroup on advisor_usergroup.a_Gid = group_list.g_Id\n"
                + "INNER JOIN user_list ON user_list.u_Id = advisor_usergroup.a_Uid\n"
                + "WHERE group_list.g_Name ='%s'", name);

        System.out.println(sql);

        try {
            rs = sta.executeQuery(sql);
            if (rs.next() == false) {
                System.out.println("查無資料");
            } else {

                do { //開始輸出
                    user_list.add(rs.getString("u_Email") + "," + rs.getString("u_Name"));
                } while (rs.next());

                //System.out.println(msg);
            }
        } catch (SQLException ex) {
            System.out.println("資料庫操作出問題:" + ex.toString());
        }
        return user_list;

    }

    public ResultSet getSearchUser() throws SQLException {
        String sql = String.format("SELECT *\n"
                + "FROM user_list");
        System.out.println(sql);

            rs = sta.executeQuery(sql);
            if (rs.next() == false) {
                System.out.println("查無資料");
            } else {
                return rs;
            }
            return null;
    }

    public ResultSet getSearchUser(String keyword) throws SQLException {
        String sql = String.format("SELECT user_list.*\n"
                + "FROM user_list\n"
                + "WHERE u_Name LIKE '%%%s%%' OR u_Email LIKE '%%%s%%' OR u_Phone LIKE '%%%s%%'", keyword, keyword, keyword);
        System.out.println(sql);

            rs = sta.executeQuery(sql);
            if (rs.next() == false) {
                System.out.println("查無資料");
            } else {
                return rs;
            }
return null;
    }

    public ResultSet getSearchKindUser(String kind) throws SQLException {
        String sql = String.format("SELECT user_list.*\n"
                + "FROM user_list\n"
                + "INNER JOIN advisor_usergroup on advisor_usergroup.a_Uid = user_list.u_Id\n"
                + "INNER JOIN group_list ON group_list.g_Id = advisor_usergroup.a_Gid\n"
                + "WHERE group_list.g_Name = '%s'", kind);
        System.out.println(sql);

            rs = sta.executeQuery(sql);
            if (rs.next() == false) {
                System.out.println("查無資料");
            } else {
                return rs;
            }
return null;
    }

    public ResultSet getSearchKindUser(String Kind, String keyword) throws SQLException {
        String sql = String.format("SELECT user_list.*\n"
                + "FROM user_list\n"
                + "INNER JOIN advisor_usergroup on advisor_usergroup.a_Uid = user_list.u_Id\n"
                + "INNER JOIN group_list ON group_list.g_Id = advisor_usergroup.a_Gid\n"
                + "WHERE (u_Name LIKE '%%%s%%' OR u_Email LIKE '%%%s%%' OR u_Phone LIKE '%%%s%%') AND group_list.g_Name = '%s'", keyword, keyword, keyword, Kind);
        System.out.println(sql);

  
            rs = sta.executeQuery(sql);
            if (rs.next() == false) {
                System.out.println("查無資料");
            } else {
                return rs;
            }

        return null;
    }

}
