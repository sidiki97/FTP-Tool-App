package com.vault.ftp.ftptool.Models;


public class ListFTP {
    private String itemPath = "";

    private Boolean recursive;

    public String getItemPath() {
        return itemPath;
    }

    public Boolean getRecursive() {
        return recursive;
    }

    public void setItemPath(String itemPath) {
        this.itemPath = itemPath;
    }

    public void setRecursive(Boolean recursive) {
        this.recursive = recursive;
    }
}
