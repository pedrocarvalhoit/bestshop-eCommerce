package com.bestshop.admin.category;

import com.bestshop.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryReposirotyTest {

    @Autowired
    private CategoryReposiroty reposiroty;

    @Test
    public void createRootCategory(){
        Category category = new Category("House");
        Category savedCategory = reposiroty.save(category);

        assertTrue(savedCategory.getId() > 1);
    }

    @Test
    @Rollback(value = false)
    public void creatSubCategory(){
        Category parent = new Category(28);
        Category subCategory = new Category("Iphone", parent);

        reposiroty.save(subCategory);
    }

    @Test
    public void getCategoryAndChildren(){
        Category category = reposiroty.findById(1).get();
        System.out.println(category.getName());

        System.out.println(category.getChildren());
        assertNotNull(category.getChildren());
    }

    @Test
    public void testPrintHierarchicalCategories() {
        Iterable<Category> categories = reposiroty.findAll();

        for (Category category : categories) {
            if (category.getParent() == null) {
                System.out.println(category.getName());

                Set<Category> children = category.getChildren();

                for (Category subCategory : children) {
                    System.out.println("--" + subCategory.getName());
                    printChildren(subCategory, 1);
                }
            }
        }
    }

    private void printChildren(Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            for (int i = 0; i < newSubLevel; i++) {
                System.out.print("--");
            }

            System.out.println(subCategory.getName());
            printChildren(subCategory, newSubLevel);
        }
    }



}