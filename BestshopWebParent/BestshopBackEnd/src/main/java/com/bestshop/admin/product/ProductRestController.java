package com.bestshop.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {

    @Autowired
    ProductService service;

    @PostMapping("/products/check_unique")
    public ResponseEntity<String> checkUniquenessOfProduct(Integer id, String name){
        if (service.checkUnique(id, name) == "Duplicate"){
            return ResponseEntity.ok("Duplicate");
        }else {
            return ResponseEntity.ok("OK");
        }
    }

}
