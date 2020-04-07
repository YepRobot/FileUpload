package com.yan.dao.impl;

import com.yan.dao.ConnectionDerbyDB;
import com.yan.dao.IMyFileDao;
import com.yan.domain.MyFile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MyFileImpl implements IMyFileDao {
    String jdbcDriver = "org.apache.derby.jdbc.EmbeddedDriver";
    Connection conn;
    Statement st;
    PreparedStatement ps;
    ResultSet rs;
    boolean flag;

    @Override//保存上传的文件信息
    public boolean saveFile(MyFile file) {
        flag = false;
        try {
            conn = ConnectionDerbyDB.getDerbyConnection();

            String sql = "insert into file_info values(?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, file.getFileID());
            ps.setString(2, file.getFileName());
            ps.setString(3, file.getFileType());
            ps.setString(4, file.getFileCreateTime());
            ps.setString(5, file.getFileFolderPath());
            ps.setString(6, file.getEncDigEnve());
            int state = ps.executeUpdate();
            if (state == 1) {
                flag = true;
            } else {
                flag = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionDerbyDB.close(conn, ps, rs);
        }
        return flag;
    }

    @Override//获取上传的文件信息
    public MyFile getFile(String id) {
        MyFile file = new MyFile();
        try {
            conn = ConnectionDerbyDB.getDerbyConnection();
            String sql = "select * from file_info where fileID=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                file.setFileID(rs.getString("fileID"));
                file.setFileName(rs.getString("fileName"));
                file.setFileType(rs.getString("fileType"));
                file.setFileCreateTime(rs.getString("fileCreateTime"));
                file.setFileFolderPath(rs.getString("fileFolderPath"));
                file.setEncDigEnve(rs.getString("EncDigEnve"));
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            ConnectionDerbyDB.close(conn, ps, rs);
        }
        return file;
    }

    @Override//列出所有文件
    public List<MyFile> listFile() {
        MyFile file = null;
        List<MyFile> list = new ArrayList<MyFile>();
        try {
            conn = ConnectionDerbyDB.getDerbyConnection();
            String sql = "select * from file_info";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                file = new MyFile();
                file.setFileID(rs.getString("fileID"));
                file.setFileName(rs.getString("fileName"));
                file.setFileType(rs.getString("fileType"));
                file.setFileCreateTime(rs.getString("fileCreateTime"));
                file.setFileFolderPath(rs.getString("fileFolderPath"));
                file.setEncDigEnve(rs.getString("encDigEnve"));
                list.add(file);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            ConnectionDerbyDB.close(conn, ps, rs);
        }
        return list;
    }
}

