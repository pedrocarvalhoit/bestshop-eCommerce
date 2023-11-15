package com.bestshop.category;

import com.bestshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listNoChildrenCategories(){
        return categoryRepository.findAllEnabled().stream()
                .filter(category -> category.getChildren() == null || category.getChildren().isEmpty())
                .collect(Collectors.toList());

    }

}
