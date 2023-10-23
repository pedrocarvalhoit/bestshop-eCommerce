package com.bestshop.admin.product;

import com.bestshop.admin.FileUploadUtil;
import com.bestshop.admin.brand.BrandService;
import com.bestshop.admin.category.CategoryService;
import com.bestshop.common.dto.ProductExibitionDto;
import com.bestshop.common.dto.ProductSaveDto;
import com.bestshop.common.entity.Brand;
import com.bestshop.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    @Autowired
    ProductService service;

    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;

    private static final String REDIRECT_PRODUCTS = "redirect:/products";

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        return listByPage(1, model);
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model) {
        Page<ProductExibitionDto> listProductsPage = service.findAllProducts(pageNum);
        List<ProductExibitionDto> listProducts = listProductsPage.getContent();

        model.addAttribute("listProducts", listProducts);

        return "products/products";
    }


    @PostMapping("/products/save")
    public String saveProduct(ProductSaveDto productSaveDto, @RequestParam("fileImage") MultipartFile mainImageMultipart,
                              @RequestParam("extraImage") MultipartFile[] extraImageMultiparts,
                              RedirectAttributes ra) throws IOException {
        Product savedProduct = service.saveWithImages(productSaveDto, mainImageName(mainImageMultipart), extraImageNames(extraImageMultiparts));

        saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

        ra.addFlashAttribute("message", "Product Created Succeffuly");
        return REDIRECT_PRODUCTS;
    }

    private void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipart.getOriginalFilename()));
            String uploadDir = "../product-images/" + savedProduct.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
        }

        if (extraImageMultiparts.length > 0) {
            String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";

            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        }
    }

    private List<String> extraImageNames(MultipartFile[] extraImageMultiparts){
        return Arrays.stream(extraImageMultiparts)
                .filter(multipartFile -> !multipartFile.isEmpty())
                .map(multipartFile -> StringUtils.cleanPath(multipartFile.getOriginalFilename()))
                .collect(Collectors.toList());
    }

    private String mainImageName(MultipartFile mainImageMultipart) {
        String fileName = " ";
        if (!mainImageMultipart.isEmpty()) {
            fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipart.getOriginalFilename()));
        }
        return fileName;
    }


    @GetMapping({"/products/new", "/products/edit/{id}"})
    public String createProduct(@PathVariable(required = false, name = "id") Integer id,
                                Model model) throws ProductNotFoundException {
        ProductSaveDto productSaveDto = ProductSaveDto.empty();

        if (id == null) {//New Product
            productSaveDto = new ProductSaveDto(null, null, null,
                    null, null, true, true,
                    null, null, null, null, null
                    , null, null, null, null, "/images/image-thumbnail.png");
        } else {//Edit existing product
            Product existingProduct = service.findById(id);
            productSaveDto = productSaveDto.fromProduct(existingProduct);
        }

        List<Brand> listBrands = brandService.listAll(Sort.by("name").ascending());

        model.addAttribute("productSaveDto", productSaveDto);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create New Product");

        return "products/product_form";
    }

    @GetMapping("/products/{id}/enabled/{enabled}")
    public String updateProductStatus(@PathVariable(name = "id") Integer id, @PathVariable(name = "enabled") boolean enabled, RedirectAttributes ra) {
        service.updtadeStatus(id, enabled);

        String message = enabled ? "Product ID: " + id + " has been Enabled" : "Product ID: " + id + " has been Disabled";
        ra.addFlashAttribute("message", message);

        return REDIRECT_PRODUCTS;
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProducto(@PathVariable(name = "id") Integer id, RedirectAttributes ra) throws ProductNotFoundException {
        try {
            service.deleteProductById(id);
            String productImagesDir = "../product-images/" + id;
            String productExtrasImageDir = "../product-images/" + id + "/extras";
            FileUploadUtil.cleanDir(productImagesDir);
            FileUploadUtil.deleteDir(productImagesDir);
            FileUploadUtil.cleanDir(productExtrasImageDir);
            FileUploadUtil.deleteDir(productExtrasImageDir);

            ra.addFlashAttribute("message", "Product ID: " + id + " has been deleted");
        } catch (ProductNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }

        return REDIRECT_PRODUCTS;
    }

}
