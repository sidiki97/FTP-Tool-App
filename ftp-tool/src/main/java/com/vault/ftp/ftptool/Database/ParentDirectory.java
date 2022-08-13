package com.vault.ftp.ftptool.Database;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ParentDirectory {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String parent;
    private String dirPath;

    protected ParentDirectory(){}

    public ParentDirectory(String parent, String dirPath){
        this.parent = parent;
        this.dirPath = dirPath;
    }

    public Long getId() {
        return id;
    }

    public String getParent() {
        return parent;
    }

    public String getDirPath() {
        return dirPath;
    }


}
