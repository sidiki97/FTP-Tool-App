package com.vault.ftp.ftptool.Service;

import com.vault.ftp.ftptool.Models.*;
import com.veeva.vault.vapil.api.client.VaultClient;
import com.veeva.vault.vapil.api.client.VaultClientBuilder;
import com.veeva.vault.vapil.api.client.VaultClientId;
import com.veeva.vault.vapil.api.model.response.*;
import com.veeva.vault.vapil.api.request.FileStagingRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
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

    public String getUrl(){
        return vaultClient.getVaultUrl();
    }

    public String getDNS(){
        return vaultClient.getVaultDNS();
    }

    public String getUsername(){
        return vaultClient.getUsername();
    }

    public List<AuthenticationResponse.Vault> getUrls(){
        List<AuthenticationResponse.Vault> vaultAuths = vaultClient.getAuthenticationResponse().getVaultIds();


//        for(AuthenticationResponse.Vault auth : vaultAuths){
//            auth.setUrl(auth.getUrl().substring(8, auth.getUrl().length() - 4));
//        }

        return vaultAuths;
    }


    public FileStagingItemResponse createFTP(CreateFTP createFTP){

        if (createFTP.getInputPath().length() == 0){
            createFTP.setInputPath(null);
        }

        FileStagingItemResponse fileStagingItemResponse = vaultClient.newRequest(FileStagingRequest.class)
                .setInputPath(createFTP.getInputPath())
                .setOverwrite(createFTP.getOverwrite())
                .createFolderOrFile(kindMap.get(createFTP.getKind()), createFTP.getFtpPath());

        return fileStagingItemResponse;


    }

    public FileStagingJobResponse deleteFTP(DeleteFTP deleteFTP){
        FileStagingJobResponse fileStagingJobResponse = vaultClient.newRequest(FileStagingRequest.class)
                .setRecursive(deleteFTP.getRecursive())
                .deleteFolderOrFile(deleteFTP.getFtpPath());

        /*
        Getting Exception with Recursive flag
         Vault Exception OPERATION_IN_PROGRESS Another [POST] operation on [{directory/}] or destination in progress

         */
        // TODO: add recursive ability
        // TODO: prevent deletion of root

        return fileStagingJobResponse;
    }

    public FileStagingItemBulkResponse listFTP(ListFTP listFTP, boolean initial){

        if (initial){
            listFTP.setRecursive(false);
        }
        else if (listFTP.getRecursive() == null){
            listFTP.setRecursive(false);
        }
        FileStagingItemBulkResponse fileStagingItemBulkResponse = vaultClient.newRequest(FileStagingRequest.class)
                .setRecursive(listFTP.getRecursive())
                .listItemsAtAPath(listFTP.getItemPath());

        return fileStagingItemBulkResponse;
    }

    public VaultResponse getItemFTP(GetItemFTP getItemFTP){
        VaultResponse vaultResponse = vaultClient.newRequest(FileStagingRequest.class)
                .setRange("bytes=0-100000")
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

    public FileStagingSessionResponse createRUS(CreateResumableUS createResumableUS){
//        float fileSize = (float)createResumableUS.getFileSize();
//        if (!((System.getProperty("os.name").substring(0,3)) == "Mac")){
//            fileSize = (fileSize / (1024*1024)) * 1000 * 1000;
//        }
//        int actualFileSize = (int)fileSize;
        FileStagingSessionResponse fileStagingSessionResponse = vaultClient.newRequest(FileStagingRequest.class)
                .createResumableUploadSession(createResumableUS.getFileStagingPath(),createResumableUS.getFileSize());

        return fileStagingSessionResponse;
    }

    public FileStagingSessionResponse uploadUS(UploadSessionFile sessionFile, byte[] fileContent) throws IOException {
        // TODO: Use program to split files or ask user to split files
        // TODO: Figure out a way to run this API consecutively with split files

        FileStagingSessionResponse response = vaultClient.newRequest(FileStagingRequest.class)
                .setFile(sessionFile.getFilename(), fileContent)
                .uploadToASession(sessionFile.getSessionId(), sessionFile.getPartNum());

        return response;
    }

    public FileStagingSessionBulkResponse listUS(){
        FileStagingSessionBulkResponse response = vaultClient.newRequest(FileStagingRequest.class)
                .listUploadSessions();

        return response;
    }

    public VaultResponse deleteUS(UploadSession uploadSession){
        VaultResponse response = vaultClient.newRequest(FileStagingRequest.class)
                .abortUploadSession(uploadSession.getSessionId());

        return response;
    }

    /*
    Idea for Upload Session endpoint:

    - Get file input
    - Get size of file
    - Desired size of parts (5 MB to 50 MB)
    - Determine number of parts (Max 2000 per upload session)
    - Need Session ID

    Basically:
    - Call upload session api consecutively until all parts are done, then call commit upload session endpoint
    - Avoid repeated user actions - automate process

    Process
    - Upload File
    - Code to determine filesize
    - If less than 50 Mb then ask to upload with regular create file staging api
    - Then take size / 5 = num parts
    - If num parts > 2000, then divide by 10
    - loop increase divider by 5 until reach 50 and while num parts > 2000
    - if still num part > 2000, then say upload cannot be done since file is too large

    - if sucessfully determined num parts, now divide size by num parts
    - round up number every part except last will take this number of bytes

    File need to be converted to byte array
    - when setting file


    Other App work
    - Validation before submit: Auth, other areas as needed
    - Select Database for Session Ids in resumable upload session
     */


    public FileStagingSessionResponse getUSDetails(UploadSession uploadSession){
        FileStagingSessionResponse response = vaultClient.newRequest(FileStagingRequest.class)
                .getUploadSessionDetails(uploadSession.getSessionId());

        return response;
    }


    public VaultClientId getVaultClientId() {
        return vaultClientId;
    }




}
