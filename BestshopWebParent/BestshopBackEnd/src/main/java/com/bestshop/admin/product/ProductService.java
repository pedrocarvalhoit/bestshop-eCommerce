package com.bestshop.admin.product;

import com.bestshop.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.NoSuchElementException;

@Service
public class ProductService {

    @Autowired
    ProductRepository repository;

    private static final int NUMBER_ITEM_PER_PAGE = 5;

    public Page<Product> findAllProducts(int pageNum) {
        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_ITEM_PER_PAGE, sort);

        return repository.findAll(pageable);
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }

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

}
