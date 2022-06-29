package com.vault.ftp.ftptool.Models;


import org.springframework.stereotype.Component;

@Component
public class Authentication {
    private String userName;
    private String passWord;
    private String vaultDNS;

    public String getUserName() {
        return userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public String getVaultDNS() {
        return vaultDNS;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public void setVaultDNS(String vaultDNS) {
        this.vaultDNS = vaultDNS;
    }
}
