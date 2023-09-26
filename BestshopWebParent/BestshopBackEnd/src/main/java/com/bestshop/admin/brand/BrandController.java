package com.bestshop.admin.brand;

import com.bestshop.admin.category.service.CategoryService;
import com.bestshop.common.entity.Brand;
import com.bestshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class BrandController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;

    @GetMapping("/brands")
    public String listAll(Model model){
        List<Brand> listBrands = brandService.findAll();
        model.addAttribute("listBrands", listBrands);

        return "brands/brands";
    }

    @PostMapping("/brands/save")
    public String createBrand(Brand brand){
        Brand savedBrand = brandService.save(brand);

        return "redirect:/brands";
    }

    @GetMapping("/brands/new")
    public String brandForm(Model model){
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("brand", new Brand());
        model.addAttribute("listCategories" ,listCategories);

        return "brands/brand_form";
    }

}
