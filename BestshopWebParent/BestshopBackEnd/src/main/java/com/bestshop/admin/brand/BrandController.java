package com.bestshop.admin.brand;

import com.bestshop.admin.AmazonS3Util;
import com.bestshop.admin.FileUploadUtil;
import com.bestshop.admin.category.CategoryService;
import com.bestshop.admin.paging.PagingAndSortingHelper;
import com.bestshop.admin.paging.PagingAndSortingParam;
import com.bestshop.admin.setting.SettingService;
import com.bestshop.common.entity.Brand;
import com.bestshop.common.entity.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Objects;

@Controller
public class BrandController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;

    @Autowired
    SettingService settingService;

    private String defaultRedirectURL = "redirect:/brands/page/1?sortField=name&sortDir=asc";


    @GetMapping("/brands")
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/brands/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listBrands", moduleURL = "/brands") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum
    ) {
        brandService.listByPage(pageNum, helper);
        return "brands/brands";
    }

    @GetMapping("/brands/new")
    public String brandForm(Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("brand", new Brand());
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Brand");

        return "brands/brand_form";
    }

    @PostMapping("/brands/save")
    public String saveBrand(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile,
                            RedirectAttributes ra) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            brand.setLogo(fileName);

            Brand savedBrand = brandService.save(brand);
            String uploadDir = "brand-logos/" + savedBrand.getId();

            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());

        } else {
            brandService.save(brand);
        }

        ra.addFlashAttribute("message", "The brand has been saved successfully.");
        return defaultRedirectURL;
    }

    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id, Model model,
                            RedirectAttributes ra) {
        try {
            Brand brand = brandService.get(id);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("brand", brand);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")");

            return "brands/brand_form";
        } catch (BrandNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("brands/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id") Integer id, RedirectAttributes ra) throws BrandNotFoundException {
        try {
            brandService.delete(id);

            String brandDir = "brand-logos/" + id;
            AmazonS3Util.removeFolder(brandDir);

            ra.addFlashAttribute("message", "Brand with id: " + id + " has been deleted successfuly");
            return defaultRedirectURL;
        } catch (BrandNotFoundException exception) {
            ra.addFlashAttribute("message", exception.getMessage());
            return defaultRedirectURL;
        }

    }

    @GetMapping("/brands/export/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        List<Brand> brandList = brandService.listAll();

        BrandCsvExporter exporter = new BrandCsvExporter();
        exporter.export(brandList, response, "brands");
    }

}
