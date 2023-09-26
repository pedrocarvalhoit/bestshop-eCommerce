package com.bestshop.admin.brand;

import com.bestshop.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BrandController {

    @Autowired
    BrandRepository brandRepository;

    @GetMapping("/brands")
    public String listAll(Model model){
        List<Brand> listBrands = brandRepository.findAll();
        model.addAttribute("listBrands", listBrands);

        return "brands/brands";
    }

}
