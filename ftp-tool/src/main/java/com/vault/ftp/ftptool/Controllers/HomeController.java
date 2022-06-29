package com.vault.ftp.ftptool.Controllers;

import com.vault.ftp.ftptool.Models.Authentication;
import com.vault.ftp.ftptool.Models.CreateFTP;
import com.vault.ftp.ftptool.Models.DeleteFTP;
import com.vault.ftp.ftptool.Models.ListFTP;
import com.vault.ftp.ftptool.Service.Services;
import com.veeva.vault.vapil.api.model.response.AuthenticationResponse;
import com.veeva.vault.vapil.api.model.response.FileStagingItemBulkResponse;
import com.veeva.vault.vapil.api.model.response.FileStagingItemResponse;
import com.veeva.vault.vapil.api.model.response.FileStagingJobResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.exceptions.TemplateProcessingException;

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
        Authentication auth = new Authentication();
        model.addAttribute("auth",auth);
        return "auth";
    }

    @PostMapping("/authproc")
    public String authProc(@ModelAttribute Authentication auth, Model model){
        AuthenticationResponse response = services.authenticate(auth);

        if (response.hasErrors()){
            model.addAttribute("errorMessage",response.getErrors().get(0).getMessage());
            return "home";
        }
        return "authproc";

    }

    @GetMapping("/ftpoptions")
    public static String ftpOptions(){
        return "ftpoptions";
    }

    @GetMapping("/createftp")
    public String createFTP(Model model){
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

    @RequestMapping(method = RequestMethod.POST, value = "/listsftpproc")
    public String ftpListProc(@ModelAttribute ListFTP listFTP, Model model){
        FileStagingItemBulkResponse fileStagingItemBulkResponse = services.listFTP(listFTP);
            if (fileStagingItemBulkResponse.hasErrors()){
                model.addAttribute("errorMessage", fileStagingItemBulkResponse.getErrors().get(0).getMessage());
                return "listftp";
            }
            return "listftpproc";
        }






}
