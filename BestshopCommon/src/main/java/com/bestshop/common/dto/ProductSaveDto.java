package com.bestshop.common.dto;

import com.bestshop.common.entity.Brand;
import com.bestshop.common.entity.Category;
import com.bestshop.common.entity.Product;

import java.math.BigDecimal;

public record ProductSaveDto(
        Integer id,
        String name,
        String alias,
        Brand brand,
        Category category,
        Boolean enabled,
        Boolean inStock,
        BigDecimal cost,
        BigDecimal price,
        BigDecimal discountPercent,
        String shortDescription,
        String fullDescription,
        Float length,
        Float width,
        Float heigth,
        Float weigth,
        String mainImagePath)
{
    public static ProductSaveDto empty() {
        return new ProductSaveDto(null,null,null,null,null,null,null,null,null
                ,null,null,null,null,null,null,null,null);
    }

    public ProductSaveDto fromProduct(Product existingProduct) {
        return new ProductSaveDto(existingProduct.getId(), existingProduct.getName(), existingProduct.getAlias(),
                existingProduct.getBrand(), existingProduct.getCategory(),
                existingProduct.isEnabled(), existingProduct.isInStock(), existingProduct.getCost(),
                existingProduct.getPrice(), existingProduct.getDiscountPercent(), existingProduct.getShortDescription(),
                existingProduct.getFullDescription(), existingProduct.getLength(), existingProduct.getWidth(),
                existingProduct.getHeigth(), existingProduct.getWeigth(), existingProduct.getMainImage());
    }
    
}
