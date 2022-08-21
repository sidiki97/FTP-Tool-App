package com.vault.ftp.ftptool.Controllers;

import com.vault.ftp.ftptool.Models.*;
import com.vault.ftp.ftptool.Service.Services;
import com.veeva.vault.vapil.api.model.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Issues Noticed:
- Get Item content does not appear return item content, only possibility appears to be able to return binary content
- Issue with recursive delete API
- TODO:
    - Idea: db connection to store file staging server directories names
    - When updating file/folder retrieve directories for parent directories input

  TODO: Resumable upload session
    - Get session details in list page
    -

 Todo: Resolve create folder issue
 */

@Controller
public class HomeController {

    @Autowired
    private Services services;

    private boolean initial = true;

    private String prevList;

    @GetMapping
    public String homePage(){
        return "home";
    }



    @GetMapping("/auth")
    public String authPage(Model model){

        model.addAttribute("auth", new Authentication());

        return "auth";
    }



    @RequestMapping(method = RequestMethod.POST, value = "/auth")
    public String authProc(@ModelAttribute("auth") Authentication auth,  Model model){


        try{
            AuthenticationResponse response = services.authenticate(auth);
            if (response.hasErrors()){
                model.addAttribute("errorMessage",response.getErrors().get(0).getMessage());
                return "auth";
            }

            model.addAttribute("url", services.getUrl());
            model.addAttribute("vault_dns", services.getDNS());
            List<AuthenticationResponse.Vault> urls = services.getUrls();
            model.addAttribute("urls", urls);
            return "ftpoptions";

        } catch (NullPointerException e){
            model.addAttribute("errorMessage", "DNS not recognized");
            return "auth";
        }

    }

    @GetMapping("/ftpoptions")
    public String ftpOptions(Model model){
        initial = true;
        prevList = "/";
        List<AuthenticationResponse.Vault> urls = services.getUrls();
        model.addAttribute("urls", urls);
        model.addAttribute("url", services.getUrl());
        model.addAttribute("vault_dns", services.getDNS());
        model.addAttribute("auth", new Authentication());

        return "ftpoptions";
    }


    @GetMapping("/createftp")
    public String createFTP(Model model){
        CreateFTP createFTP = new CreateFTP();
        model.addAttribute("createFTP",createFTP);
        return "createftp";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/createftpproc")
    public String ftpCreateProc(@ModelAttribute CreateFTP createFTP, Model model) {
        initial = true;
        try
        {

            FileStagingItemResponse fileStagingItemResponse = services.createFTP(createFTP);
            if (fileStagingItemResponse.hasErrors()){
                model.addAttribute("errorMessage", fileStagingItemResponse.getErrors().get(0).getMessage());
                return "createftp";
            }
            model.addAttribute("kind", fileStagingItemResponse.getData().getKind());
            model.addAttribute("path", fileStagingItemResponse.getData().getPath());
            model.addAttribute("name", fileStagingItemResponse.getData().getName());
            if (!(fileStagingItemResponse.getData().getKind() == "file")){
                model.addAttribute("size", fileStagingItemResponse.getData().getSize());
                model.addAttribute("md5", fileStagingItemResponse.getData().getFileContentMd5());
            }
//            model.addAttribute("size", fileStagingItemResponse.getData().getSize());
//            model.addAttribute("md5", fileStagingItemResponse.getData().getFileContentMd5());

            return "createftpproc";
        }
        catch (NullPointerException exception){
            model.addAttribute("errorMessage", "No such file or directory");
            return "createftp";
        }


    }

    @PostMapping("/delete")
    public String delete(@ModelAttribute("deleteFTP") DeleteFTP deleteFTP, Model model){

        String paths = deleteFTP.getFtpPath();
        List<String> pathArray = Arrays.asList(paths.split(","));

        for (String s : pathArray) {
            deleteFTP.setFtpPath(s);
            deleteFTP.setRecursive(true);
            FileStagingJobResponse response = services.deleteFTP(deleteFTP);
        }

        model.addAttribute("paths", pathArray);

        return "delete";

    }

    @PostMapping("/update")
    public String update(@ModelAttribute("item") UpdateFTP updateFTP, Model model) throws InterruptedException{

        String paths = updateFTP.getPath();
        List<String> pathArray = Arrays.asList(paths.split(","));
        String parent = updateFTP.getNewParent();

        for (String s : pathArray) {

            updateFTP.setPath(s);
            updateFTP.setNewParent(parent);
            FileStagingJobResponse response = services.updateItemFTP(updateFTP);
            if (response.isSuccessful()){
                Thread.sleep(1000);
            }
            if (response.hasErrors()){
                model.addAttribute("paths", pathArray);
                return "failedupdate";
            }

        }

        model.addAttribute("paths", pathArray);

        return "update";

    }

    @GetMapping("/deleteftp")
    public String deleteFTP(Model model){
        DeleteFTP deleteFTP = new DeleteFTP();
        model.addAttribute("deleteFTP",deleteFTP);
        return "deleteftp";
    }

    @PostMapping("/deleteftpproc")
    public String ftpDeleteProc(@ModelAttribute("deleteFTP") DeleteFTP deleteFTP, Model model){
        FileStagingJobResponse fileStagingJobResponse = services.deleteFTP(deleteFTP);
        if (fileStagingJobResponse.hasErrors()){
            model.addAttribute("errorMessage", fileStagingJobResponse.getErrors().get(0).getMessage());
            return "deleteftp";
        }
        model.addAttribute("jobId", fileStagingJobResponse.getData().getJobId());
        initial = true;
        return "deleteftpproc";
    }

    @GetMapping("/listftp")
    public String listFTP(Model model){
        if (initial){
            initial = false;
            ListFTP initialList = new ListFTP();

            FileStagingItemBulkResponse fileStagingItemBulkResponse = services.listFTP(initialList, true);
            model.addAttribute("createFTP",new CreateFTP());
            model.addAttribute("item", new UpdateFTP());
            model.addAttribute("deleteFTP", new DeleteFTP());
            model.addAttribute("listFTP", new ListFTP());
            model.addAttribute("response",fileStagingItemBulkResponse.getData());


            return "listftpproc";
        }

        model.addAttribute("listFTP",new ListFTP());
        return "listftp";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/listftpproc")
    public String ftpListProc(@ModelAttribute("listFTP") ListFTP listFTP, Model model){


        FileStagingItemBulkResponse fileStagingItemBulkResponse = services.listFTP(listFTP, false);

        if (fileStagingItemBulkResponse.hasErrors()){
            model.addAttribute("errorMessage", fileStagingItemBulkResponse.getErrors().get(0).getMessage());
            return "listftp";
        }
        prevList = listFTP.getItemPath();
        model.addAttribute("val", listFTP.getItemPath());
        model.addAttribute("createFTP",new CreateFTP());
        model.addAttribute("item", new UpdateFTP());
        model.addAttribute("deleteFTP", new DeleteFTP());
        model.addAttribute("listFTP", new ListFTP());
        model.addAttribute("response",fileStagingItemBulkResponse.getData());
//        model.addAttribute("directoryList", sortDir(fileStagingItemBulkResponse.getData()));

        return "listftpproc";
    }

    @GetMapping("/prevlistftp")
    public String prevlistFTP(Model model){
        ListFTP priorList = new ListFTP();
        priorList.setItemPath(prevList);
        FileStagingItemBulkResponse fileStagingItemBulkResponse = services.listFTP(priorList, true);
        model.addAttribute("val", prevList);
        model.addAttribute("createFTP",new CreateFTP());
        model.addAttribute("item", new UpdateFTP());
        model.addAttribute("deleteFTP", new DeleteFTP());
        model.addAttribute("listFTP", new ListFTP());
        model.addAttribute("response",fileStagingItemBulkResponse.getData());


        return "listftpproc";


    }

//    public List<String> sortDir(List<FileStagingItemBulkResponse.FileStagingItem> items){
//        List<String> paths = new ArrayList<>();
//        for (int i = 0; i < items.size(); i++) {
//            if (items.get(i).getKind().equals("folder")) {
//                paths.add(items.get(i).getPath());
//            }
//        }
//
//        return paths;
//    }


    // Unable to retrieve item content with API
//    @GetMapping("/getitem")
//    public String getItemFTP(Model model){
//        GetItemFTP getItemFTP = new GetItemFTP();
//        model.addAttribute("getitem", getItemFTP);
//        return "getitem";
//    }
//
//    @RequestMapping(method = RequestMethod.POST, value = "/getitemftpproc")
//    public String ftpgetItemProc(@ModelAttribute("getitem") GetItemFTP getItemFTP, Model model){
//
//
//        VaultResponse vaultResponse = services.getItemFTP(getItemFTP);
//        if(vaultResponse.hasErrors()){
//            model.addAttribute("errorMessage", vaultResponse.getErrors().get(0).getMessage());
//            return "getitem";
//        }
//        model.addAttribute("response",vaultResponse);
//        return "getitemftpproc";
//
//
//    }


    @GetMapping("/updateitem")
    public String updateItemFTP(Model model){
        UpdateFTP updateFTP = new UpdateFTP();
        model.addAttribute("item", updateFTP);
        return "updateitem";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateitemproc")
    public String ftpUpdateItemProc(@ModelAttribute("item") UpdateFTP updateFTP, Model model){

        FileStagingJobResponse response = services.updateItemFTP(updateFTP);
        if (response.hasErrors()){
            model.addAttribute("errorMessage", response.getErrors().get(0).getMessage());
            return "updateitem";
        }
        model.addAttribute("jobId", response.getData().getJobId());
        initial = true;
        return "updateitemproc";


    }


    /*

    Resumable Upload APIs

     */

    @GetMapping("/uploadsession")
    public String uploadSess(){
        // TODO: Add option to go back to File Staging APIs
        return "uploadsession";
    }

    @GetMapping("/createRUS")
    public String createRUS(Model model){
        model.addAttribute("newUS", new CreateResumableUS());
        return "createRUS";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/createRUSproc")
    public String createRUSproc(@ModelAttribute("newUS") CreateResumableUS resumableUS, Model model){

        FileStagingSessionResponse fileStagingSessionResponse = services.createRUS(resumableUS);
        if (fileStagingSessionResponse.hasErrors()){
            model.addAttribute("errorMessage", fileStagingSessionResponse.getErrors().get(0).getMessage());
            return "createRUS";
        }
        model.addAttribute("sessionId", fileStagingSessionResponse.getData().getId());
        return "createRUSproc";
    }

    @GetMapping("/uploadRUS")
    public String uploadRUS(Model model){
        model.addAttribute("uploadUS", new UploadSessionFile());
        return "uploadRUS";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/uploadRUSproc")
    public String uploadRUSproc(@ModelAttribute("uploadUS") UploadSessionFile sessionFile, Model model) throws IOException {
        Path path = Paths.get(sessionFile.getFilePath());
        byte[] fileContent = Files.readAllBytes(path);

        // byte conversion from decimal to binary in case Mac OS
//        float fileSize = (float)sessionFile.getFileSize();
//        if (!((System.getProperty("os.name").substring(0,3)) == "Mac")){
//            fileSize = (fileSize / (1024*1024)) * 1000 * 1000;
//        }
//        int actualFileSize = (int)fileSize;
//
//        sessionFile.setFileSize(actualFileSize);

        FileStagingSessionResponse response = services.uploadUS(sessionFile, fileContent);
        if (response.hasErrors()){
            model.addAttribute("errorMessage", response.getErrors().get(0).getMessage());
            return "uploadRUS";
        }

        return "uploadRUSproc";
    }


// TODO: Get Item endpoint
//    @RequestMapping(method = RequestMethod.POST, value = "/uploadRUSproc")
//    public String uploadRUSproc(@ModelAttribute("uploadUS") UploadSessionFile sessionFile, Model model) throws IOException {
//
//        int fileSize = sessionFile.getFileSize();
//        // byte conversion from decimal to binary in case Mac OS
//        if ((System.getProperty("os.name").substring(0,3)) == "Mac"){
//            fileSize = (fileSize / (1024*1024)) * 1000 * 1000;
//        }
//
//        Path path = Paths.get(sessionFile.getFilePath());
//        byte[] fileContent = Files.readAllBytes(path);
//
//
//
//        final int bytesPerSplit = 1024 * 1024 * 10;
//        int numParts = fileSize/(bytesPerSplit);
//
//        final int remainingBytes = fileSize % bytesPerSplit;
//
//        int part = 1;
//
//        for (;part < numParts; part++){
//            FileStagingSessionResponse response = services.uploadUS(sessionFile, part, fileContent);
//            if (response.hasErrors()){
//                model.addAttribute("errorMessage", response.getErrors().get(0).getMessage());
//                return "uploadRUS";
//            }
//
//        }
//
//    }



    @GetMapping("/listUS")
    public String listUploadSessions(Model model){
        FileStagingSessionBulkResponse response = services.listUS();
        model.addAttribute("data", response.getData());
        model.addAttribute("newUS", new CreateResumableUS());
        model.addAttribute("abortId",new UploadSession());
        return "listUS";
    }

    @GetMapping("/abortsession")
    public String abortUploadSessionP(Model model){
        model.addAttribute("abortId",new UploadSession());
        return "abortsession";
    }

    @PostMapping("/abortsessionproc")
    public String abortSessionProc(@ModelAttribute("abortId") UploadSession uploadSession, Model model) {
        VaultResponse response = services.deleteUS(uploadSession);
        if (response.hasErrors()){
            model.addAttribute("errorMessage", response.getErrors().get(0).getMessage());
            return "abortsession";
        }
        return "abortsessionproc";
    }

    @GetMapping("/getsessiondetail")
    public String getUploadSession(Model model){
        model.addAttribute("sessId",new UploadSession());
        return "getsessiondetail";
    }

    @PostMapping("/sessiondetail")
    public String uploadSession(@ModelAttribute("sessId") UploadSession uploadSession, Model model) {
        FileStagingSessionResponse response = services.getUSDetails(uploadSession);
        if (response.hasErrors()){
            model.addAttribute("errorMessage", response.getErrors().get(0).getMessage());
            return "getsessiondetail";
        }
        model.addAttribute("data", response.getData());
        return "sessiondetail";
    }

    @GetMapping("/listfileparts")
    public String getFileParts(Model model){
        model.addAttribute("sessId",new UploadSession());
        return "listfileparts";
    }

    @PostMapping("/listfiledetail")
    public String fileParts(@ModelAttribute("sessId") UploadSession uploadSession, Model model) {
        FileStagingSessionResponse response = services.getUSDetails(uploadSession);
        if (response.hasErrors()){
            model.addAttribute("errorMessage", response.getErrors().get(0).getMessage());
            return "listfileparts";
        }
        model.addAttribute("data", response.getData());
        return "listfiledetail";
    }


    @GetMapping("/error")
    public String error(Model model){
        return "error";
    }



}
