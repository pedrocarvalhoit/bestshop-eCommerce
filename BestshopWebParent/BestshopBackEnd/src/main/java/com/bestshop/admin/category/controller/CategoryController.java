package com.bestshop.admin.category.controller;

import com.bestshop.admin.FileUploadUtil;
import com.bestshop.admin.category.CategoryNotFoundException;
import com.bestshop.admin.category.CategoryService;
import com.bestshop.common.entity.Category;
import com.itextpdf.text.pdf.qrcode.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.UnknownServiceException;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping("/categories")
    public String listAll(Model model){
        List<Category> categoryList = service.listAll();

        model.addAttribute("listCategories", categoryList);

        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String createCategoryForm(Model model){
        List<Category> categoryList = service.listCategoriesUsedInForm();


        model.addAttribute("category", new Category());
        model.addAttribute("listCategories", categoryList);
        model.addAttribute("pageTitle", "Create New Category");

        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String createCategory(Category category, @RequestParam("fileImage")MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException {
        if(!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());//Get the original name of the image
            category.setImage(fileName);
     //       boolean registeredCategory = service.checkExistingCategory(category);

            Category savedCategory = service.save(category);
            String uploadDir = "../category-images/" + savedCategory.getId();//Sets the file upload direction

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);//Saves the file

      //      String message = registeredCategory ? "Category " + savedCategory.getName().toUpperCase() + " updated successfully" : "Category created successfully";

            redirectAttributes.addFlashAttribute("message", "Category created successfully");

        }else{
            service.save(category);
            redirectAttributes.addFlashAttribute("message", "Category created successfully");
        }
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes ra) throws CategoryNotFoundException {
        try{
            Category category = service.get(id);
            List<Category> listCategories = service.listCategoriesUsedInForm();

            model.addAttribute("category", category);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Edit the category with Name: " + category.getName());

            return "categories/category_form";
        }catch (CategoryNotFoundException exception){
            ra.addFlashAttribute("message", exception.getMessage());
            return "redirect:/categories";
        }
    }


    @GetMapping("/categories/{id}/enabled/{enabled}")
    public String changeEnabled(@PathVariable(name = "id") Integer id, @PathVariable(name = "enabled") boolean enabled, Model model, RedirectAttributes redirectAttributes){
        service.updateEnabled(id, enabled);

        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) throws CategoryNotFoundException{
        try{
            Category categoryToDelete = service.get(id);
            String uploadDir = "../category-images/" + categoryToDelete.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.deleteDir(uploadDir);
            service.deleteCategory(id);
            redirectAttributes.addFlashAttribute("message", "Category " + categoryToDelete.getName().toUpperCase() + " has been delted");

        }catch (CategoryNotFoundException exception){
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }


        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public void expotToCsv(){
        List<Category> categoryList = service.listAll();
    }

}
