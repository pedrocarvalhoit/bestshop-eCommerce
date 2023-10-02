package com.bestshop.admin.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BrandRestController {

    @Autowired
    BrandService service;

    @PostMapping("/brands/check_unique")
    public ResponseEntity<String> checkUnique(@Param("id")Integer id, @Param("name")String name){

        if (service.checkUnique(id, name) == "Duplicate"){
            return ResponseEntity.ok("Duplicate");
        }else {
            return ResponseEntity.ok("OK");
        }
    }

}
