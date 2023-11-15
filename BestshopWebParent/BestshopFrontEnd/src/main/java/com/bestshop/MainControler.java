package com.bestshop;

import com.bestshop.category.CategoryService;
import com.bestshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainControler {

    @Autowired
    private CategoryService service;

    @GetMapping("/")
    public String viewHomePage (Model model){
        List<Category> listCategories = service.listNoChildrenCategories();
        model.addAttribute("listCategories", listCategories);

        return "index";    }

}
