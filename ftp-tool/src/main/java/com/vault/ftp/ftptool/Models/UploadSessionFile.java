package com.vault.ftp.ftptool.Models;

public class UploadSessionFile {
    private int fileSize;
    private String filename;
    private String filePath;
    private String sessionId;
    private int partNum;


    public int getFileSize() {
        return fileSize;
    }

    public String getFilename() {
        return filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getPartNum() {
        return partNum;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setPartNum(int partNum) {
        this.partNum = partNum;
    }
}

