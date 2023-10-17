package com.bestshop.admin.product;

import com.bestshop.common.dto.ProductExibitionDto;
import com.bestshop.common.dto.ProductSaveDto;
import com.bestshop.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class ProductService {

    @Autowired
    ProductRepository repository;

    private static final int NUMBER_ITEM_PER_PAGE = 5;

    public Page<ProductExibitionDto> findAllProducts(int pageNum) {
        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_ITEM_PER_PAGE, sort);

        return repository.findAll(pageable)
                .map(product -> new ProductExibitionDto(product.getId(), product.getName(), product.getBrand(),
                        product.getCategory(), product.isEnabled()));
    }

    public Product save(ProductSaveDto productSaveDto) {
        Product product = new Product(productSaveDto);

        if (productSaveDto.id() == null) {
            product.setCreatedTime(LocalDateTime.now());
        }

        if (productSaveDto.alias().isEmpty() || productSaveDto.alias() == null) {
            String defaultAlias = productSaveDto.name().replace(" ", "-").toLowerCase();
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(productSaveDto.alias().replace(" ", "-").toLowerCase());
        }

        return repository.save(product);
    }

    public String checkUnique(Integer id, String name) {
        boolean newProduct = (id == null || id == 0);
        Product product = repository.findByName(name);

        if (newProduct && (product != null)) {
            return "Duplicate";
        }else {
            if (product != null && product.getId() != id){
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

        if (countById == 0 || countById == null){
            throw new ProductNotFoundException("Could not find any product with ID: " + id);
        }

        repository.deleteById(id);
    }
}
