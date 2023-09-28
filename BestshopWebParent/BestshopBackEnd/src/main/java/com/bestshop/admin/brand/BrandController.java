package com.bestshop.admin.brand;

import com.bestshop.admin.FileUploadUtil;
import com.bestshop.admin.category.service.CategoryService;
import com.bestshop.common.entity.Brand;
import com.bestshop.common.entity.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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

    @GetMapping("/brands/new")
    public String brandForm(Model model){
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("brand", new Brand());
        model.addAttribute("listCategories" ,listCategories);
        model.addAttribute("pageTitle", "Create New");

        return "brands/brand_form";
    }

    @PostMapping("/brands/save")
    public String createBrand(Brand brand, @RequestParam("logoImage")MultipartFile multipartFile, RedirectAttributes ra) throws IOException {
        if(!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);

            Brand savedBrand = brandService.save(brand);
            String uploadDir = "../brand-logos/" + savedBrand.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        }else {
            brandService.save(brand);
        }
        ra.addFlashAttribute("message", "Brand has been saved successfully");
        return "redirect:/brands";
    }

    @GetMapping("brands/edit/{id}")
    public String editBrand(@PathVariable(name = "id")Integer id, Model model, RedirectAttributes ra){
        try{
            Brand brand = brandService.get(id);

            model.addAttribute("brand", brand);
            model.addAttribute("pageTitle", "Edit the brand with Name: " + brand.getName());

            return "brands/brand_form";
        }catch (BrandNotFoundException exception){
            ra.addFlashAttribute("message", exception.getMessage());
            return "brands/brand_form";
        }
    }

    @GetMapping("brands/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id")Integer id, RedirectAttributes ra){
        brandService.delete(id);

        ra.addFlashAttribute("message", "Brand with id: " + id + " has been deleted successfuly");

        return "redirect:/brands";
    }

    @GetMapping("/brands/export/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        List<Brand> brandList = brandService.listAll(Sort.by("name").ascending());

        BrandCsvExporter exporter = new BrandCsvExporter();
        exporter.export(brandList, response, "brands");
    }

}
