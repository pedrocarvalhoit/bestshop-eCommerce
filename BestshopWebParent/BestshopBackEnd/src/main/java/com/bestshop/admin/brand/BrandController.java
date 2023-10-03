package com.bestshop.admin.brand;

import com.bestshop.admin.FileUploadUtil;
import com.bestshop.admin.category.CategoryService;
import com.bestshop.common.entity.Brand;
import com.bestshop.common.entity.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
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

    private static final String REDIRECT_BRANDS = "redirect:/brands";

    @GetMapping("/brands")
    public String listFirstPage(Model model){
        return listByPage(1, "name", "asc", null, model);
    }

    @GetMapping("/brands/page/{page}")
    public String listByPage(@PathVariable(name = "page")Integer pageNum ,
                             @Param("sortField")String sortField,
                             @Param("sortDir")String sortDir,
                             @Param("keyword")String keyword, Model model){
        Page<Brand> pageBrand = brandService.listByPage(pageNum, sortField, sortDir, keyword);
        List<Brand> listBrands = pageBrand.getContent();

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        int startCount = (pageNum -1) * BrandService.NUMBER_ITEM_PER_PAGE +1;
        long endCount = startCount + BrandService.NUMBER_ITEM_PER_PAGE -1;
        if(endCount > pageBrand.getTotalElements()){
            endCount = pageBrand.getTotalElements();
        }

        model.addAttribute("listBrands", listBrands);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalPages", pageBrand.getTotalPages());
        model.addAttribute("totalItems", pageBrand.getTotalElements());
        model.addAttribute("keyword", keyword);

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
    public String saveBrand(Brand brand, @RequestParam("fileImage")MultipartFile multipartFile,
                            RedirectAttributes ra) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            brand.setLogo(fileName);

            Brand savedBrand = brandService.save(brand);
            String uploadDir = "../brand-logos/" + savedBrand.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } else {
            brandService.save(brand);
        }

        ra.addFlashAttribute("message", "The brand has been saved successfully.");
        return REDIRECT_BRANDS;
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
            return REDIRECT_BRANDS;
        }
    }

    @GetMapping("brands/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id")Integer id, RedirectAttributes ra) throws BrandNotFoundException {
        try{
            String uploadDir = "../brand-logos/" + id;
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.deleteDir(uploadDir);

            brandService.delete(id);
            ra.addFlashAttribute("message", "Brand with id: " + id + " has been deleted successfuly");
            return REDIRECT_BRANDS;
        }catch (BrandNotFoundException exception){
            ra.addFlashAttribute("message", exception.getMessage());
            return REDIRECT_BRANDS;
        }

    }

    @GetMapping("/brands/export/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        List<Brand> brandList = brandService.listAll(Sort.by("name").ascending());

        BrandCsvExporter exporter = new BrandCsvExporter();
        exporter.export(brandList, response, "brands");
    }

}
