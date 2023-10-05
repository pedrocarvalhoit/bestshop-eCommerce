package com.bestshop.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    ProductService service;

    @GetMapping("/products")
    public String listByPage(Model model){
        Page<ProductExibitionDto> listProductsPage = service.findAllProducts();
        List<ProductExibitionDto> listProducts = listProductsPage.getContent();

        model.addAttribute("listProducts", listProducts);

        return "products/products";
    }

    @PostMapping("/products/new")
    public String createProduct(){


        return "products/product_form";
    }

}
