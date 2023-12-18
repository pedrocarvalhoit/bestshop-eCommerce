package com.bestshop.admin.category;

import com.bestshop.admin.AmazonS3Util;
import com.bestshop.admin.FileUploadUtil;
import com.bestshop.common.entity.Category;
import com.bestshop.common.exception.CategoryNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class CategoryController {

    public static final int ROOT_CATEGORIES_PER_PAGE = 4;

    @Autowired
    private CategoryService service;

    @GetMapping("/categories")
    public String listFirstPage(String sortDir, Model model) {
        return listByPage(1, sortDir, null, model);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum,
                             String sortDir, String keyword, Model model) {
        if (sortDir ==  null || sortDir.isEmpty()) {
            sortDir = "asc";
        }

        CategoryPageInfo pageInfo = new CategoryPageInfo();
        List<Category> listCategories = service.listByPage(pageInfo, pageNum, sortDir, keyword);

        long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
        if (endCount > pageInfo.getTotalElements()) {
            endCount = pageInfo.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", "name");
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("moduleURL", "/categories");

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
            boolean registeredCategory = service.checkExistingCategory(category);

            Category savedCategory = service.save(category);
            String uploadDir = "category-images/" + savedCategory.getId();//Sets the file upload direction

            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());//Saves the file

        }else{
            service.save(category);
        }
        redirectAttributes.addFlashAttribute("message", "Category has been saved successfully");
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

        String message = enabled ? "Category id " + id + " has been Enabled" : "Category id " + id + " has been Disabled";
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) throws CategoryNotFoundException{
        try{
            service.deleteCategory(id);
            String categoryDir = "category-images/" + id;
            AmazonS3Util.removeFolder(categoryDir);

            redirectAttributes.addFlashAttribute("message", "Category ID: " + id + " has been delted");

        }catch (CategoryNotFoundException exception){
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public void expotToCsv(HttpServletResponse response) throws IOException {
        List<Category> categoryList = service.listAll(Sort.by("name").ascending());
        CategoryCsvExporter exporter = new CategoryCsvExporter();
        exporter.export(categoryList, response, "categories");
    }

}
