package com.vault.ftp.ftptool.Models;


import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Component
public class Authentication {
    @NotEmpty(message = "Username is required.")
    private String userName;
    @NotEmpty(message = "Password is required.")
    private String passWord;
    @NotEmpty(message = "Vault DNS is required.")
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
