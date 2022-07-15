package com.vault.ftp.ftptool.Service;

import com.vault.ftp.ftptool.Models.*;
import com.veeva.vault.vapil.api.client.VaultClient;
import com.veeva.vault.vapil.api.client.VaultClientBuilder;
import com.veeva.vault.vapil.api.client.VaultClientId;
import com.veeva.vault.vapil.api.model.response.*;
import com.veeva.vault.vapil.api.request.FileStagingRequest;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class Services {


    private VaultClientId vaultClientId = new VaultClientId("Veeva", "Vault",
            "Platform", true, "FTP App");

    private VaultClient vaultClient;

    private Map<String, FileStagingRequest.Kind> kindMap = Map.of(
            "File", FileStagingRequest.Kind.FILE,
            "Folder", FileStagingRequest.Kind.FOLDER);

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

    //TODO: Add check valid session to prevent access to other ftp pages

    public FileStagingItemResponse createFTP(CreateFTP createFTP){

        if (createFTP.getInputPath().length() == 0){
            createFTP.setInputPath(null);
        }

        FileStagingItemResponse fileStagingItemResponse = vaultClient.newRequest(FileStagingRequest.class)
                .setInputPath(createFTP.getInputPath())
                .createFolderOrFile(kindMap.get(createFTP.getKind()), createFTP.getFtpPath());

        //TODO: look into adding setfile function
        //TODO: ask to overwrite

        return fileStagingItemResponse;


    }

    public FileStagingJobResponse deleteFTP(DeleteFTP deleteFTP){
        FileStagingJobResponse fileStagingJobResponse = vaultClient.newRequest(FileStagingRequest.class)
                .deleteFolderOrFile(deleteFTP.getFtpPath());

        // TODO: add recursive ability
        // TODO: prevent deletion of root

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
