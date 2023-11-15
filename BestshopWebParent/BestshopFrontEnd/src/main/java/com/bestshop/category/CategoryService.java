package com.bestshop.category;

import com.bestshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public Category getCategory(String alias) {
        return categoryRepository.findByAliasEnabled(alias);
    }

    public List<Category> getCategoryParents(Category child) {
        List<Category> listParents = new ArrayList<>();

        Category parent = child.getParent();

        while (parent != null) {
            listParents.add(0, parent);
            parent = parent.getParent();
        }

        listParents.add(child);

        return listParents;
    }

}
