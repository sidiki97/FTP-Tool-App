package com.vault.ftp.ftptool.Models;

public class DeleteFTP {

    private String ftpPath;
    private Boolean recursive;

    public String getFtpPath() {
        return ftpPath;
    }

    public Boolean getRecursive() {
        return recursive;
    }

    public void setFtpPath(String ftpPath) {
        this.ftpPath = ftpPath;
    }

    public void setRecursive(Boolean recursive) {
        this.recursive = recursive;
    }
}
