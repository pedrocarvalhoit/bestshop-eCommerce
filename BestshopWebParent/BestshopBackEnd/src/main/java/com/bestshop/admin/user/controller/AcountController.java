package com.bestshop.admin.user.controller;

import com.bestshop.admin.user.repository.UserRepository;
import com.bestshop.admin.user.service.UserService;
import com.bestshop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Objects;

@Controller
public class AcountController {

    @Autowired
    UserService service;

    @Autowired
    UserRepository repository;

    @GetMapping("/account")
    public String viewDetails(Authentication authentication, Model model){
        String userEmail = authentication.getName();
        User loggedUser = service.findByUsername(userEmail);
        String rolesList = String.valueOf(loggedUser.getRoles());

        model.addAttribute("user", loggedUser);
        model.addAttribute("userFullName", loggedUser.getFullName());
        model.addAttribute("rolesList", rolesList);

        return "users/account_form";
    }

    @PostMapping("/account/updateDetails")
    public String updateUserData(User userData, @RequestParam("image")MultipartFile multipartFile, Authentication authentication, Model model, RedirectAttributes redirectAttributes) throws IOException {
        String userEmail = authentication.getName();
        User loggedUser = service.findByUsername(userEmail);

        if (!Objects.equals(userData.getPassword(), userData.getConfirmPassword())){
            redirectAttributes.addFlashAttribute("message", "Passwords don't match");
            return "redirect:/users/account/viewDetails/";
        }

        service.updateDetails(loggedUser, userData);

        if(!multipartFile.isEmpty()){
            service.savePhoto(loggedUser, multipartFile);
        }

        repository.save(loggedUser);

        redirectAttributes.addFlashAttribute("messageSuccess", "Your account details have been updated");

        return "redirect:/account";
    }
}
