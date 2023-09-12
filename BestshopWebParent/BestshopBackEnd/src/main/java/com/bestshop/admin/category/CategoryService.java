package com.bestshop.admin.category;

import com.bestshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
@Transactional
public class CategoryService {

    public static final int CATEGORIES_PER_PAGE = 6;

    @Autowired
    private CategoryReposiroty reposiroty;

    public List<Category> listAll(){
        return reposiroty.findAll(Sort.by("name").ascending());
    }

    public List<Category> listCategoriesUsedInForm(){
        List<Category> categoriesUsedInForm = new ArrayList<Category>();

        Iterable<Category> categoriesInDb = reposiroty.findAll();

        for (Category category : categoriesInDb) {
            if (category.getParent() == null) {
                categoriesUsedInForm.add(Category.copyIdAndName(category));

                Set<Category> children = category.getChildren();

                for (Category subCategory : children) {
                    String name = "--" + subCategory.getName();
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

                    listChildren(categoriesUsedInForm, subCategory, 1);
                }
            }
        }

        return categoriesUsedInForm;
    }

    public Category save(Category category) {
        return reposiroty.save(category);
    }

    public void updateEnabled(Integer id, boolean enabled) {
        Category category = reposiroty.findById(id).get();
        category.setEnabled(enabled);
        reposiroty.save(category);
    }

    public void deleteCategory(Integer id) {
        Category category = reposiroty.findById(id).get();
        reposiroty.deleteById(id);
    }

    public Category get(Integer id) {
        return reposiroty.findById(id).get();
    }

    private void listChildren(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

            listChildren(categoriesUsedInForm, subCategory, newSubLevel);
        }
    }
}
