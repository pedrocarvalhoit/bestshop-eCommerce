package com.bestshop.common.dto;

import com.bestshop.common.entity.Brand;
import com.bestshop.common.entity.Category;

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
        Float weigth)
{
}
