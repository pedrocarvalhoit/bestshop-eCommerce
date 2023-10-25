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
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

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
                        product.getCategory(), product.isEnabled(), product.getMainImagePath()));
    }

    public Product save(ProductSaveDto dto) {
        return repository.save(new Product(dto));
    }

    public Product save(ProductSaveDto dto, String mainImage, List<String> extraImageNames,
                        String[] detailNames, String[] detailValues) {
        Product product = new Product(dto);
        product.setMainImage(mainImage);
        extraImageNames.forEach(product::addExtraImage);

        setProductDetails(detailNames, detailValues, product);

        if (dto.id() == null) {
            product.setCreatedTime(LocalDateTime.now());
        }

        if (dto.alias() == null || dto.alias().isEmpty()) {
            String defaultAlias = dto.name().replace(" ", "-").toLowerCase();
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(dto.alias().replace(" ", "-").toLowerCase());
        }

        return repository.save(product);
    }

    private void setProductDetails(String[] detailNames, String[] detailValues, Product product){
        if (detailNames == null || detailNames.length == 0) return;
        for (int i = 0; i < detailNames.length; i++) {
            String name = detailNames[i];
            String value = detailValues[i];

            if (!name.isEmpty() || !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }

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
