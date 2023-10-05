package com.bestshop.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    ProductService service;

    @GetMapping("/products")
    public String listByPage(Model model){
        List<ProductExibitionDto> listProducts = service.findAllProducts();

        model.addAttribute("listProducts", listProducts);

        return "products/products";
    }

}
