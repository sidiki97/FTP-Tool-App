package com.vault.ftp.ftptool.Service;

import com.vault.ftp.ftptool.Controllers.HomeController;
import com.vault.ftp.ftptool.Models.*;
import com.veeva.vault.vapil.api.client.VaultClient;
import com.veeva.vault.vapil.api.client.VaultClientBuilder;
import com.veeva.vault.vapil.api.client.VaultClientId;
import com.veeva.vault.vapil.api.model.response.*;
import com.veeva.vault.vapil.api.request.FileStagingRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.http.HttpRequest;

@Service
public class Services {


    private VaultClientId vaultClientId = new VaultClientId("Veeva", "Support",
            "Platform", true, "FTP App");

    private VaultClient vaultClient;

    public AuthenticationResponse authenticate(Authentication authentication){
        vaultClient = VaultClientBuilder
                .newClientBuilder(VaultClient.AuthenticationType.BASIC)
                .withVaultDNS(authentication.getVaultDNS())
                .withVaultUsername(authentication.getUserName())
                .withVaultPassword(authentication.getPassWord())
                .withVaultClientId(vaultClientId)
                .build();

        return vaultClient.getAuthenticationResponse();
    }



    public FileStagingItemResponse createFTP(CreateFTP createFTP){

        FileStagingItemResponse fileStagingItemResponse = vaultClient.newRequest(FileStagingRequest.class)
                    .setInputPath(createFTP.getInputPath())
                    .createFolderOrFile(createFTP.getKind(), createFTP.getFtpPath());

        return fileStagingItemResponse;


    }

    public FileStagingJobResponse deleteFTP(DeleteFTP deleteFTP){
        FileStagingJobResponse fileStagingJobResponse = vaultClient.newRequest(FileStagingRequest.class)
                .deleteFolderOrFile(deleteFTP.getFtpPath());

        return fileStagingJobResponse;
    }

    public FileStagingItemBulkResponse listFTP(ListFTP listFTP){
        FileStagingItemBulkResponse fileStagingItemBulkResponse = vaultClient.newRequest(FileStagingRequest.class)
                .listItemsAtAPath(listFTP.getItemPath());

        return fileStagingItemBulkResponse;
    }

    public VaultResponse getItemFTP(GetItemFTP getItemFTP){
        VaultResponse vaultResponse = vaultClient.newRequest(FileStagingRequest.class)
                .setRange("bytes=0-1000")
                .getItemContent(getItemFTP.getPath());

        return vaultResponse;
    }


    public FileStagingJobResponse updateItemFTP(UpdateFTP updateFTP){
        FileStagingJobResponse fileStagingJobResponse = vaultClient.newRequest(FileStagingRequest.class)
                .setParent(updateFTP.getNewParent())
                .setName(updateFTP.getNewName())
                .updateFolderOrFile(updateFTP.getPath());

        return fileStagingJobResponse;
    }

    public VaultClientId getVaultClientId() {
        return vaultClientId;
    }


}
