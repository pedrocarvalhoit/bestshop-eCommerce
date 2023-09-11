package com.bestshop.admin.category;

import com.bestshop.common.entity.Category;
import com.itextpdf.text.pdf.qrcode.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    CategoryService service;

    @Autowired
    CategoryReposiroty reposiroty;

    @GetMapping("/categories")
    public String listAll(Model model){
        List<Category> categoryList = service.listAll();
        model.addAttribute("categoryList", categoryList);

        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String createCategoryForm(Model model){
        Category category = new Category();
        model.addAttribute("category", category);
        category.setEnabled(true);

        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String createCategory(Category category){
        Category savedCategory = new Category(category.getName(), category.getAlias(), category.getImage());

        reposiroty.save(savedCategory);
        return "redirect:/categories";
    }

}
