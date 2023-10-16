package com.bestshop.admin.product;

import com.bestshop.admin.brand.BrandService;
import com.bestshop.admin.category.CategoryService;
import com.bestshop.common.dto.ProductExibitionDto;
import com.bestshop.common.dto.ProductSaveDto;
import com.bestshop.common.entity.Brand;
import com.bestshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    ProductService service;

    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;

    @GetMapping("/products")
    public String listFirstPage(Model model){
        return  listByPage(1, model);
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum")int pageNum, Model model){
        Page<ProductExibitionDto> listProductsPage = service.findAllProducts(pageNum);
        List<ProductExibitionDto> listProducts = listProductsPage.getContent();

        model.addAttribute("listProducts", listProducts);

        return "products/products";
    }

    @PostMapping("/products/save")
    public String saveProduct(Model model, ProductSaveDto productSaveDto, RedirectAttributes ra){
        service.save(productSaveDto);

        ra.addFlashAttribute("message", "Product Created Succeffuly");

        return "redirect:/products";
    }

    @GetMapping("/products/new")
    public String createProduct(Model model){
        List<Brand> listBrands = brandService.listAll(Sort.by("name").ascending());

        model.addAttribute("productSaveDto", new ProductSaveDto(null, null, null,
                null, null, true, true,
                null, null, null, null, null
                ,null, null, null, null));

        model.addAttribute("listBrands" ,listBrands);
        model.addAttribute("pageTitle", "Create New Product");

        return "products/product_form";
    }

}
