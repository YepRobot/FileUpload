package com.yan.dao;

import com.yan.domain.MyFile;

import java.sql.SQLException;
import java.util.List;

public interface IMyFileDao {
    public boolean saveFile(MyFile file) throws SQLException;
    public MyFile getFile(String id);
    public List<MyFile> listFile();
}
