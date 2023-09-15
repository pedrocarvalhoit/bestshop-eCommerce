package com.bestshop.admin.category;

import com.bestshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class CategoryService {

    public static final int CATEGORIES_PER_PAGE = 6;

    @Autowired
    private CategoryReposiroty repository;

    public List<Category> listAll() {
        List<Category> rootCategories = repository.findRootCategories();
        return listHierarchicalCategories(rootCategories);
    }

    public List<Category> listHierarchicalCategories(List<Category> rootCategories) {

        return rootCategories.stream()// Create a stream of rootCategories and flatten the results using flatMap
                .flatMap(category -> listSubHierarchicalCategories(category, 0).stream())// For each root category, call the listSubHierarchicalCategories method
                .collect(Collectors.toList());// Collect all the results into a single list
    }


    public Category save(Category category) {
        return repository.save(category);
    }

    public void updateEnabled(Integer id, boolean enabled) {
        Category category = repository.findById(id).get();
        category.setEnabled(enabled);
        repository.save(category);
    }

    public void deleteCategory(Integer id) {
        Category category = repository.findById(id).get();
        repository.deleteById(id);
    }

    public Category get(Integer id) throws CategoryNotFoundException{
        try{
            return repository.findById(id).get();
        }catch (NoSuchElementException exception){
            throw new CategoryNotFoundException("Could not find any category with id: " + id);
        }
    }

    /**
     * Fetches a list of categories to be used in a form.
     * The list includes parent categories and their subcategories.
     *
     * @return a list of categories with '--' repeated prefix indicating their level
     */
    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        repository.findAll().stream()
                .filter(category -> category.getParent() == null)
                .forEach(category -> processSubCategories(categoriesUsedInForm, category, 0));

        return categoriesUsedInForm;
    }

    /**
     * Processes a category and its children recursively, adding them to the provided list.
     *
     * @param list     the list to add the categories to
     * @param category the current category being processed
     * @param level    the current level (depth) of the category in the hierarchy
     */
    private void processSubCategories(List<Category> list, Category category, int level) {
        list.add(Category.copyIdAndName(category.getId(), "--".repeat(level) + category.getName()));
        category.getChildren().forEach(child -> processSubCategories(list, child, level + 1));
    }

    private List<Category> listSubHierarchicalCategories(Category parent, int subLevel) {
        List<Category> hierarchicalCategories = new ArrayList<>();// Create a list to store hierarchical categories for the current branch
        String prefix = "--".repeat(subLevel);// Create a prefix string to indicate the level of indentation

        hierarchicalCategories.add(Category.copyFull(parent, prefix + parent.getName()));// Add the current parent category with the appropriate indentation to the list

        Set<Category> children = parent.getChildren(); // Retrieve the children of the parent category
        int newSubLevel = subLevel + 1;// Increment the subLevel to indicate we are going one level deeper in the hierarchy

        children.forEach(subCategory -> {// Process each child category using a forEach loop
            hierarchicalCategories.addAll(listSubHierarchicalCategories(subCategory, newSubLevel));// Add the child category with the updated indentation to the list
        });

        return hierarchicalCategories;// Return the hierarchicalCategories list for the current branch
    }

    public boolean checkExistingCategory(Category category) {
        if (category.getId() == null) {
            return false;
        }
        return repository.findById(category.getId()).isPresent();
    }

    public String checkUnique(Integer id, String name, String alias){
        boolean isANewCategory = (id == null || id == 0);
        Category categoryByName = repository.findByName(name);

        if (isANewCategory){
            if (categoryByName != null){
                return "DuplicatedName";
            }else {
                Category categoryByAlias = repository.findByAlias(alias);
                if (categoryByAlias != null){
                    return "DuplicatedAlias";
                }
            }
        }else {
            if (categoryByName != null && categoryByName.getId() != id) {
                return "DuplicateName";
            }

            Category categoryByAlias = repository.findByAlias(alias);
            if (categoryByAlias != null && categoryByAlias.getId() != id) {
                return "DuplicateAlias";
            }

        }

        return "OK";
    }

}
