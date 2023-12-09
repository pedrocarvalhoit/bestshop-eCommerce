package com.bestshop.admin.product;

import com.bestshop.common.entity.product.Product;
import com.bestshop.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/products/get/{id}")
    public ProductDTO getProductInfo(@PathVariable("id") Integer id)
            throws ProductNotFoundException {
        Product product = service.get(id);
        return new ProductDTO(product.getName(), product.getMainImagePath(),
                product.getDiscountPrice(), product.getCost());
    }

}
