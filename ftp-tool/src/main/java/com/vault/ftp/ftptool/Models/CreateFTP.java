package com.vault.ftp.ftptool.Models;

import com.veeva.vault.vapil.api.request.FileStagingRequest;

public class CreateFTP {
    private String inputPath;
    private FileStagingRequest.Kind kind = FileStagingRequest.Kind.FILE;
    private String ftpPath;

    public String getInputPath() {
        return inputPath;
    }

    public FileStagingRequest.Kind getKind() {
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

    public String extractFilename(String path) {
        if (path.substring(0, 12) == "C:\\fakepath\\") {
            return path.substring(12);
        }
        int x;
        x = path.lastIndexOf('/');
        if (x >= 0) // Unix-based path
            return path.substring(x+1);
        x = path.lastIndexOf('\\');
        if (x >= 0) // Windows-based path
            return path.substring(x+1);
        return path; // just the filename

    }

}
