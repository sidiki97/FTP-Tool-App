package com.vault.ftp.ftptool.Models;

public class CreateResumableUS {
    private int fileSize;
    private String fileStagingPath;
    private String filename;


    public int getFileSize() {
        return fileSize;
    }

    public String getFilename() {
        return filename;
    }

    public String getFileStagingPath() {
        return fileStagingPath;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFileStagingPath(String fileStagingPath) {
        this.fileStagingPath = fileStagingPath;
    }

}
