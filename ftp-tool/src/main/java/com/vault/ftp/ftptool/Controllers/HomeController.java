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

/*
Issues Noticed:
- Get Item content does not appear return item content, only possibility appears to be able to return binary content
TODO: Increase bytes at this endpoint and see if content returned - nope
 */

@Controller
public class HomeController {

    @Autowired
    private Services services;

    @GetMapping
    public String homePage(){
        return "home";
    }

    @GetMapping("/auth")
    public String authPage(Model model){
        // TODO: Add Validation before submit
        model.addAttribute("auth", new Authentication());
        return "auth";
    }



    @RequestMapping(method = RequestMethod.POST, value = "/auth")
    public String authProc(@ModelAttribute("auth") @Valid Authentication auth, BindingResult result, Model model){

        if (result.hasErrors()){
            return "auth";
        }

        try{
            AuthenticationResponse response = services.authenticate(auth);
            if (response.hasErrors()){
                model.addAttribute("errorMessage",response.getErrors().get(0).getMessage());
                return "home";
            }
            return "ftpoptions";
        } catch (NullPointerException e){
            model.addAttribute("errorMessage", "DNS not recognized");
            return "home";
        }

    }

    @GetMapping("/ftpoptions")
    public static String ftpOptions(){
        return "ftpoptions";
    }

    @GetMapping("/createftp")
    public String createFTP(Model model){
        // Add option of adding either file or folder
        CreateFTP createFTP = new CreateFTP();
        model.addAttribute("createFTP",createFTP);
        return "createftp";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/createftpproc")
    public String ftpCreateProc(@ModelAttribute CreateFTP createFTP, Model model){


        try
        {

            FileStagingItemResponse fileStagingItemResponse = services.createFTP(createFTP);
            if (fileStagingItemResponse.hasErrors()){
                model.addAttribute("errorMessage", fileStagingItemResponse.getErrors().get(0).getMessage());
                return "createftp";
            }
            return "createftpproc";
        }
        catch (NullPointerException exception){
            model.addAttribute("errorMessage", "No such file or directory");
            return "createftp";
        }



    }

    @GetMapping("/deleteftp")
    public String deleteFTP(Model model){
        DeleteFTP deleteFTP = new DeleteFTP();
        model.addAttribute("deleteFTP",deleteFTP);
        return "deleteftp";
    }

    @PostMapping("/deleteftpproc")
    public String ftpDeleteProc(@ModelAttribute DeleteFTP deleteFTP, Model model){
        FileStagingJobResponse fileStagingJobResponse = services.deleteFTP(deleteFTP);
        if (fileStagingJobResponse.hasErrors()){
            model.addAttribute("errorMessage", fileStagingJobResponse.getErrors().get(0).getMessage());
            return "deleteftp";
        }
        return "deleteftpproc";
    }

    @GetMapping("/listftp")
    public String listFTP(Model model){
        ListFTP listFTP = new ListFTP();
        model.addAttribute("listFTP",listFTP);
        return "listftp";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/listftpproc")
    public String ftpListProc(@ModelAttribute ListFTP listFTP, Model model){
        FileStagingItemBulkResponse fileStagingItemBulkResponse = services.listFTP(listFTP);

        if (fileStagingItemBulkResponse.hasErrors()){
            model.addAttribute("errorMessage", fileStagingItemBulkResponse.getErrors().get(0).getMessage());
            return "listftp";
        }
        model.addAttribute("response",fileStagingItemBulkResponse.getData());
        return "listftpproc";
        }

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
        return "listUS";
    }

    @GetMapping("/abortsession")
    public String abortUploadSessionP(Model model){
        model.addAttribute("abortId",new UploadSession());
        return "abortsession";
    }

    @PostMapping("/abortsessionproc")
    public String abortSessionProc(@ModelAttribute UploadSession uploadSession, Model model) {
        VaultResponse response = services.deleteUS(uploadSession);
        if (response.hasErrors()){
            model.addAttribute("errorMessage", response.getErrors().get(0).getMessage());
            return "abortsession";
        }
        return "abortsessionproc";
    }

    @GetMapping("/error")
    public String error(Model model){
        return "error";
    }



}
