package com.bestshop.admin.product;

import com.bestshop.admin.paging.PagingAndSortingHelper;
import com.bestshop.common.entity.product.Product;
import com.bestshop.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {

    @Autowired
    ProductRepository repository;

    public static final int PRODUCTS_PER_PAGE = 5;

    public List<Product> listAll() {
        return (List<Product>) repository.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper, Integer categoryId) {
        Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
        String keyword = helper.getKeyword();
        Page<Product> page = null;

        if (keyword != null && !keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
                page = repository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
            } else {
                page = repository.findAll(keyword, pageable);
            }
        } else {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
                page = repository.findAllInCategory(categoryId, categoryIdMatch, pageable);
            } else {
                page = repository.findAll(pageable);
            }
        }

        helper.updateModelAttributes(pageNum, page);
    }

    public Product save(Product product) {
        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            String defaultAlias = product.getName().replaceAll(" ", "-");
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(product.getAlias().replaceAll(" ", "-"));
        }

        product.setUpdatedTime(new Date());

        return repository.save(product);
    }

    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Product productByName = repository.findByName(name);

        if (isCreatingNew) {
            if (productByName != null) return "Duplicate";
        } else {
            if (productByName != null && productByName.getId() != id) {
                return "Duplicate";
            }
        }

        return "OK";
    }

    public void updtadeStatus(Integer id, boolean enabled) {
        Product product = repository.findById(id).get();
        product.setEnabled(enabled);
        repository.save(product);
    }

    public void deleteProductById(Integer id) throws ProductNotFoundException {
        Long countById = repository.countById(id);

        if (countById == null || countById == 0){
            throw new ProductNotFoundException("Could not find any product with ID: " + id);
        }

        repository.deleteById(id);
    }

    public Product findById(Integer id) throws ProductNotFoundException {
        if (id == null) throw new ProductNotFoundException("Product not found");
        return repository.findById(id).get();
    }

    public Product get (Integer id) throws ProductNotFoundException {
        try {
            return repository.findById(id).get();
        }catch (NoSuchElementException exception){
            throw new ProductNotFoundException("Could not found any product with id: " + id);
        }
    }

    public void saveProductPrice(Product productInForm) {
        Product productInDB = repository.findById(productInForm.getId()).get();
        productInDB.setCost(productInForm.getCost());
        productInDB.setPrice(productInForm.getPrice());
        productInDB.setDiscountPercent(productInForm.getDiscountPercent());

        repository.save(productInDB);
    }

    public void searchProducts(int pageNum, PagingAndSortingHelper helper) {
        Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
        String keyword = helper.getKeyword();
        Page<Product> page = repository.searchProductsByName(keyword, pageable);
        helper.updateModelAttributes(pageNum, page);
    }
}
