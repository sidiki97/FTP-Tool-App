package com.vault.ftp.ftptool.Models;

public class UpdateFTP {
    private String path;
    private String newParent;
    private String newName;

    public String getPath() {
        return path;
    }

    public String getNewParent() {
        return newParent;
    }

    public String getNewName() {
        return newName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setNewParent(String newParent) {
        this.newParent = newParent;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
