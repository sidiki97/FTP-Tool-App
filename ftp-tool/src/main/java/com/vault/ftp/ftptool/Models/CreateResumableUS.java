package com.vault.ftp.ftptool.Models;

public class CreateResumableUS {
    private int fileSize;
    private String fileStagingPath;
    private String filename;
    private Boolean overwrite;


    public int getFileSize() {
        return fileSize;
    }

    public String getFilename() {
        return filename;
    }

    public String getFileStagingPath() {
        return fileStagingPath;
    }

    public Boolean getOverwrite() {
        return overwrite;
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

    public void setOverwrite(Boolean overwrite) {
        this.overwrite = overwrite;
    }
}
