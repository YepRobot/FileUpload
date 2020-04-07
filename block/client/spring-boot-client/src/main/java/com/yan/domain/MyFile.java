package com.yan.domain;

public class MyFile {
    private String fileID;//UUID
    private String fileName;//原始文件名
    private String fileType;//文件类型
    private String fileCreateTime;//文件创建时间
    private String fileFolderPath;//保存目录地址
    private String encDigEnve;//加密数字信封

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileCreateTime() {
        return fileCreateTime;
    }

    public void setFileCreateTime(String fileCreateTime) {
        this.fileCreateTime = fileCreateTime;
    }

    public String getFileFolderPath() {
        return fileFolderPath;
    }

    public void setFileFolderPath(String fileFolderPath) {
        this.fileFolderPath = fileFolderPath;
    }

    public String getEncDigEnve() {
        return encDigEnve;
    }

    public void setEncDigEnve(String encDigEnve) {
        this.encDigEnve = encDigEnve;
    }

    @Override
    public String toString() {
        return "{" +
                "\"fileID\":" +"\"" +fileID +"\"" +
                ",\"fileName\":" +"\"" + fileName +"\"" +
                ",\"fileType\":" +"\"" + fileType +"\"" +
                ",\"fileCreateTime\":" +"\"" + fileCreateTime +"\"" +
                ",\"fileFolderPath\":" + "\"" +fileFolderPath +"\"" +
                ",\"encDigEnve\":" + "\"" +encDigEnve +"\"" +
                "}";
    }
}
