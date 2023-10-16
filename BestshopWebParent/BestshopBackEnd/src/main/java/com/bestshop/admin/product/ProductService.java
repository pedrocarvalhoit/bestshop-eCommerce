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

    public Page<ProductExibitionDto> findAllProducts(int pageNum){
        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_ITEM_PER_PAGE, sort);

        return repository.findAll(pageable)
                .map(product -> new ProductExibitionDto(product.getId(), product.getName(), product.getBrand(),
                        product.getCategory(), product.isEnabled()));
    }

    public Product save(ProductSaveDto productSaveDto) {
        Product product = new Product(productSaveDto);

        if (productSaveDto.id() == null){
            product.setCreatedTime(LocalDateTime.now());
        }

        if (productSaveDto.alias().isEmpty() || productSaveDto.alias() == null){
            String defaultAlias = productSaveDto.name().replace(" ", "-").toLowerCase();
            product.setAlias(defaultAlias);
        }else {
            product.setAlias(productSaveDto.alias().replace(" ", "-").toLowerCase());
        }

        return repository.save(product);
    }
}
