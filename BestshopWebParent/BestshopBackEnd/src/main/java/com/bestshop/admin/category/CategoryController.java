package com.bestshop.admin.category;

import com.bestshop.common.entity.Category;
import com.itextpdf.text.pdf.qrcode.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.UnknownServiceException;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping("/categories")
    public String listAll(Model model){
        List<Category> categoryList = service.listAll();
        model.addAttribute("categoryList", categoryList);

        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String createCategoryForm(Model model){
        List<Category> categoryList = service.listCategoriesUsedInForm();

        model.addAttribute("category", new Category());
        model.addAttribute("categoryList", categoryList);

        return "categories/category_form";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model){
        Category category = service.get(id);
        model.addAttribute("category", category);

        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String createCategory(@ModelAttribute("category") Category category,
                                 @RequestParam("image")MultipartFile multipartFile){

        service.save(category);

        return "redirect:/categories";
    }

    @GetMapping("/categories/{id}/enabled/{enabled}")
    public String changeEnabled(@PathVariable(name = "id") Integer id, @PathVariable(name = "enabled") boolean enabled){
        service.updateEnabled(id, enabled);

        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id){
        service.deleteCategory(id);

        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public void expotToCsv(){
        List<Category> categoryList = service.listAll();
    }

}
