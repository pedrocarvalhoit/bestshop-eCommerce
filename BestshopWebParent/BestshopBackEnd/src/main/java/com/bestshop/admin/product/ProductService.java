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

@Service
public class ProductService {

    @Autowired
    ProductRepository repository;

    private static final int NUMBER_ITEM_PER_PAGE = 5;

    public Page<ProductExibitionDto> findAllProducts(){
        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(0, NUMBER_ITEM_PER_PAGE, sort);

        return repository.findAll(pageable)
                .map(product -> new ProductExibitionDto(product.getId(), product.getName(), product.getBrand(),
                        product.getCategory(), product.isEnabled()));
    }

    public Product save(ProductSaveDto productSaveDto) {

        return repository.save(new Product(productSaveDto));
    }
}
