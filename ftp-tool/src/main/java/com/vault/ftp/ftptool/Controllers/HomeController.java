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
            return "authproc";
        } catch (NullPointerException e){
            model.addAttribute("errorMessage", "DNS not recognized");
            return "home";
        }

    }

    @GetMapping("/authproc")
    public String getAuthProc(){
        return "authproc";
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


    @GetMapping("/getitem")
    public String getItemFTP(Model model){
        GetItemFTP getItemFTP = new GetItemFTP();
        model.addAttribute("getitem", getItemFTP);
        return "getitem";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getitemftpproc")
    public String ftpgetItemProc(@ModelAttribute GetItemFTP getItemFTP, Model model){


        VaultResponse vaultResponse = services.getItemFTP(getItemFTP);
        if(vaultResponse.hasErrors()){
            model.addAttribute("errorMessage", vaultResponse.getErrors().get(0).getMessage());
            return "error";
        }
        model.addAttribute("response",vaultResponse);
        return "getitemftpproc";


    }


    @GetMapping("/updateitem")
    public String updateItemFTP(Model model){
        UpdateFTP updateFTP = new UpdateFTP();
        model.addAttribute("item", updateFTP);
        return "updateitem";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateitemproc")
    public String ftpUpdateItemProc(@ModelAttribute UpdateFTP updateFTP, Model model){

        FileStagingJobResponse response = services.updateItemFTP(updateFTP);
        if (response.hasErrors()){
            model.addAttribute("errorMessage", response.getErrors().get(0).getMessage());
            return "error";
        }
        return "updateitemproc";


    }

    @GetMapping("/error")
    public String error(Model model){
        return "error";
    }



}
