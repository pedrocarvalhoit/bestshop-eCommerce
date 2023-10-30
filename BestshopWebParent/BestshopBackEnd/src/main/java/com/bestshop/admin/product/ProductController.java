package com.bestshop.admin.product;

import com.bestshop.admin.FileUploadUtil;
import com.bestshop.admin.brand.BrandService;
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
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    ProductService service;

    @Autowired
    BrandService brandService;

    private static final String REDIRECT_PRODUCTS = "redirect:/products";

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        return listByPage(1, model);
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model) {
        Page<Product> listProducts = service.findAllProducts(pageNum);

        model.addAttribute("listProducts", listProducts);

        return "products/products";
    }


    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes ra,
                              @RequestParam("fileImage") MultipartFile mainImageMultipart,
                              @RequestParam("extraImage") MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues
    )
            throws IOException {
        setMainImageName(mainImageMultipart, product);
        setExtraImageNames(extraImageMultiparts, product);
        setProductDetails(detailNames, detailValues, product);

        Product savedProduct = service.save(product);

        saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

        ra.addFlashAttribute("message", "The product has been saved successfully.");

        return "redirect:/products";
    }

    private void setProductDetails(String[] detailNames, String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;

        for (int count = 0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];

            if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }
    }

    private void saveUploadedImages(MultipartFile mainImageMultipart,
                                    MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
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

    private void setExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
        if (extraImageMultiparts.length > 0) {
            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    product.addExtraImage(fileName);
                }
            }
        }
    }

    private void setMainImageName(MultipartFile mainImageMultipart, Product product) {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            product.setMainImage(fileName);
        }
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

        return "products/product_form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model,
                              RedirectAttributes ra) {
        try {
            Product product = service.get(id);
            List<Brand> listBrands = brandService.listAll();
            Integer numberOfExistingExtraImages = product.getImages().size();

            model.addAttribute("product", product);
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

            return "products/product_form";

        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());

            return "redirect:/products";
        }
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
            FileUploadUtil.cleanDir(productExtrasImageDir);
            FileUploadUtil.deleteDir(productExtrasImageDir);
            FileUploadUtil.cleanDir(productImagesDir);
            FileUploadUtil.deleteDir(productImagesDir);

            ra.addFlashAttribute("message", "Product ID: " + id + " has been deleted");
        } catch (ProductNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }

        return REDIRECT_PRODUCTS;
    }

}
