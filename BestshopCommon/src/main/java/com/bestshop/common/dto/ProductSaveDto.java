package com.bestshop.common.dto;

import java.math.BigDecimal;

public record ProductSaveDto(
        String name,
        String alias,
        String shortDescription,
        String fullDescription,
        Boolean enabled,
        Boolean inStock,
        BigDecimal cost,
        BigDecimal price,
        BigDecimal discountPercent)
{
}
