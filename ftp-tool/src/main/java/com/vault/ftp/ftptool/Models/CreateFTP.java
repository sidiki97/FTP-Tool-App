package com.vault.ftp.ftptool.Models;

import com.veeva.vault.vapil.api.request.FileStagingRequest;

import java.util.Map;

public class CreateFTP {
    private String inputPath;
    private String kind;
    private String ftpPath;



    public String getInputPath() {
        return inputPath;
    }

    public String getKind() {
        return kind;
    }

    public String getFtpPath() {
        return ftpPath;
    }

    public void setFtpPath(String ftpPath) {
        this.ftpPath = ftpPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
