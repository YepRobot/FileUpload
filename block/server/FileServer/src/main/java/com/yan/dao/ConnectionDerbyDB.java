package com.yan.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConnectionDerbyDB {

    public static Connection getDerbyConnection(){
        Connection conn=null;
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();//加载驱动
            conn = DriverManager.getConnection("jdbc:derby:TESTDB;create=true");//连接数据库
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs){
        try {
            if(conn!=null){
                conn.close();
            }
            if(ps!=null){
                ps.close();
            }
            if(rs!=null){
                rs.close();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }


    }
    public static void main(String[] args) {
        ConnectionDerbyDB.getDerbyConnection();
        System.out.println("数据库连接成功");
    }

}
