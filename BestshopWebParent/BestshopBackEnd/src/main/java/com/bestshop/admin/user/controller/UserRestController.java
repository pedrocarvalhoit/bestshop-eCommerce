package com.bestshop.admin.user.controller;

import com.bestshop.admin.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    @Autowired
    private UserService service;

    @PostMapping("/users/check_email")
    public String chekDuplicateEmail(@Param("id") Integer id, @Param("email") String email){
        return service.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
