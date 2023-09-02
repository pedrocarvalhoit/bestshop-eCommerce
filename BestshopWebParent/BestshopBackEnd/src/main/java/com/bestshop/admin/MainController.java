package com.bestshop.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    @GetMapping("/home")
    public String viewHomePage(){
        return "index";
    }

    @GetMapping("/login")
    public String viewLoginPage(){
        return "/login";
    }

    @GetMapping("/bs/logout")
    public String performLogout() {
        // .. perform logout
        return "redirect:/login&logout";
    }
}
