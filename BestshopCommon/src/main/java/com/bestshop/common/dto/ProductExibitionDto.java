package com.bestshop.common.dto;

import com.bestshop.common.entity.Brand;
import com.bestshop.common.entity.Category;
import jakarta.validation.constraints.NotBlank;

public record ProductExibitionDto(

        @NotBlank
        Integer id,

        @NotBlank
        String name,

        Brand brand,

        Category category,

        boolean enabled,

        String mainImagePath
        ) {
}
