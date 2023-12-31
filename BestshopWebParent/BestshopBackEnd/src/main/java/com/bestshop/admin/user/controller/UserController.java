package com.bestshop.admin.user.controller;

import com.bestshop.admin.AmazonS3Util;
import com.bestshop.admin.FileUploadUtil;
import com.bestshop.admin.paging.PagingAndSortingHelper;
import com.bestshop.admin.paging.PagingAndSortingParam;
import com.bestshop.admin.user.*;
import com.bestshop.admin.user.export.UserCsvExporter;
import com.bestshop.admin.user.export.UserExcelExporter;
import com.bestshop.admin.user.export.UserPDFExporter;
import com.bestshop.common.entity.Role;
import com.bestshop.common.entity.User;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    private String defaultRedirectURL = "redirect:/users/page/1?sortField=firstName&sortDir=asc";

    @GetMapping("/users")
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listUsers", moduleURL = "/users") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {
        service.listByPage(pageNum, helper);

        return "users/users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model){
        List<Role> listRoles = service.listRoles();

        User user = new User();
        user.setEnabled(true);

        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Create New User");

        return "users/user_form";
    }

    @PostMapping("users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image")MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = service.save(user);

            String uploadDir = "user-photos/" + savedUser.getId();

            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
        }else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            service.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
        return getRedirectURLToAffectedUser(user);
    }

    private String getRedirectURLToAffectedUser(User user) {
        String firstPartOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            User user = service.get(id);
            List<Role> listRoles = service.listRoles();

            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
            model.addAttribute("listRoles", listRoles);

            return "users/user_form";
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) throws UserNotFoundException {
        try{
            service.delete(id);
            String userPhotosDir = "user-photos/" + id;
            AmazonS3Util.removeFolder(userPhotosDir);

            redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");
        }catch (UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("users/{id}/enabled/{enabled}")
    public String updateUserEnabledStatus(@PathVariable(name = "id") Integer id,
                                        @PathVariable(name = "enabled") boolean enabled,
                                        RedirectAttributes redirectAttributes){
        service.updateEnabledStatu(id, enabled);
        String status = enabled ? "Enabled" : "Disabled";
        redirectAttributes.addFlashAttribute("message", "User iD " + id + " has been " + status);
        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> listUsers = service.listAll();
        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(listUsers, response, "users");
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> listUsers = service.listAll();
        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(listUsers, response, "users");
    }

    @GetMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException, DocumentException {
        List<User> listUsers = service.listAll();
        UserPDFExporter exporter = new UserPDFExporter();
        exporter.export(listUsers, response, "users");
    }


}
