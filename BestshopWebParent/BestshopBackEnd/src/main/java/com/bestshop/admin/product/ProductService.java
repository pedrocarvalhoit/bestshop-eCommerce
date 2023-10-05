package com.bestshop.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    ProductRepository repository;

    public List<ProductExibitionDto> findAllProducts(){
        return repository.findAll().stream()
                .map(product -> new ProductExibitionDto(product.getId(), product.getName(), product.getBrand(),
                        product.getCategory(), product.isEnabled()))
                .collect(Collectors.toList());
    }

}
