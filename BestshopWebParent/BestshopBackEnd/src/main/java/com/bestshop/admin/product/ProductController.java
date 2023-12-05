package com.bestshop.admin.product;

import com.bestshop.admin.FileUploadUtil;
import com.bestshop.admin.brand.BrandService;
import com.bestshop.admin.category.CategoryService;
import com.bestshop.admin.paging.PagingAndSortingHelper;
import com.bestshop.admin.paging.PagingAndSortingParam;
import com.bestshop.admin.secutiry.BestshopUserDetails;
import com.bestshop.common.entity.Brand;
import com.bestshop.common.entity.Category;
import com.bestshop.common.entity.product.Product;
import com.bestshop.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    private String defaultRedirectURL = "redirect:/products/page/1?sortField=name&sortDir=asc&categoryId=0";

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        return defaultRedirectURL;
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listProducts", moduleURL = "/products") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum, Model model,
            Integer categoryId
    ) {

        productService.listByPage(pageNum, helper, categoryId);

        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        if (categoryId != null) model.addAttribute("categoryId", categoryId);
        model.addAttribute("listCategories", listCategories);

        return "products/products";
    }


    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes ra,
                              @RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,
                              @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              @AuthenticationPrincipal BestshopUserDetails loggedUser
    )
            throws IOException {

        if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
            if (loggedUser.hasRole("Salesperson")) {
                productService.saveProductPrice(product);
                ra.addFlashAttribute("message", "The product has been saved successfully.");
                return defaultRedirectURL;
            }
        }

        ProductSaveHelper.setMainImageName(mainImageMultipart, product);
        ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
        ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts, product);
        ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = productService.save(product);

        ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

        ProductSaveHelper.deleteExtraImagesWeredRemovedOnForm(product);

        ra.addFlashAttribute("message", "The product has been saved successfully.");

        return defaultRedirectURL;
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        List<Brand> listBrands = brandService.listAll();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("numberOfExistingExtraImages", 0);

        return "products/product_form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model,
                              RedirectAttributes ra, @AuthenticationPrincipal BestshopUserDetails loggedUser) {
        try {
            Product product = productService.get(id);
            List<Brand> listBrands = brandService.listAll();
            Integer numberOfExistingExtraImages = product.getImages().size();

            boolean isReadOnlyForSalesperson = !loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")
                    && (loggedUser.hasRole("Salesperson"));

            model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);

            model.addAttribute("product", product);
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

            return "products/product_form";

        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());

            return defaultRedirectURL;
        }
    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, Model model,
                                     RedirectAttributes ra) {
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);

            return "products/product_detail_modal";

        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());

            return defaultRedirectURL;
        }
    }

    @GetMapping("/products/{id}/enabled/{enabled}")
    public String updateProductEnabledStatus(@PathVariable(name = "id") Integer id, @PathVariable(name = "enabled") boolean enabled, RedirectAttributes ra) {
        productService.updtadeStatus(id, enabled);

        String message = enabled ? "Product ID: " + id + " has been Enabled" : "Product ID: " + id + " has been Disabled";
        ra.addFlashAttribute("message", message);

        return defaultRedirectURL;
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProducto(@PathVariable(name = "id") Integer id, RedirectAttributes ra) throws ProductNotFoundException {
        try {
            productService.deleteProductById(id);
            String productImagesDir = "../product-images/" + id;
            String productExtrasImageDir = "../product-images/" + id + "/extras";
            FileUploadUtil.cleanDir(productExtrasImageDir);
            FileUploadUtil.deleteDir(productExtrasImageDir);
            FileUploadUtil.cleanDir(productImagesDir);
            FileUploadUtil.deleteDir(productImagesDir);

            ra.addFlashAttribute("message", "Product ID: " + id + " has been deleted");
        } catch (ProductNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }

        return defaultRedirectURL;
    }

}
