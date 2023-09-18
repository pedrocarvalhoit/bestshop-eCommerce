package com.bestshop.admin.category;

import com.bestshop.common.entity.Category;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class CategoryService {

    public static final int CATEGORIES_PER_PAGE = 4;

    @Autowired
    private CategoryReposiroty repository;

    public List<Category> listAll(@NotNull String sortDir) {
        Sort sort = Sort.by("name");

        if (sortDir.equals("asc")) {
            sort = sort.ascending();
        } else if (sortDir.equals("desc")) {
            sort = sort.descending();
        }

        List<Category> rootCategories = repository.findRootCategories(sort);

        return listHierarchicalCategories(rootCategories);
    }

    public Category save(Category category) {
        return repository.save(category);
    }

    public void updateEnabled(Integer id, boolean enabled) {
        Category category = repository.findById(id).get();
        category.setEnabled(enabled);
        repository.save(category);
    }

    public void deleteCategory(Integer id) throws CategoryNotFoundException{
        Long countByid = repository.countById(id);
        if (countByid == null || countByid == 0){
            throw new CategoryNotFoundException("Category id: " + id + " don´t exists");
        }
        repository.deleteById(id);
    }

    public Category get(Integer id) throws CategoryNotFoundException{
        try{
            return repository.findById(id).get();
        }catch (NoSuchElementException exception){
            throw new CategoryNotFoundException("Could not find any category with id: " + id);
        }
    }

    public List<Category> listHierarchicalCategories(List<Category> rootCategories) {
        return rootCategories.stream()// Create a stream of rootCategories and flatten the results using flatMap
                .flatMap(category -> listSubHierarchicalCategories(category, 0).stream())// For each root category, call the listSubHierarchicalCategories method
                .collect(Collectors.toList());// Collect all the results into a single list
    }

    /**
     * Fetches a list of categories to be used in a form.
     * The list includes parent categories and their subcategories.
     *
     * @return a list of categories with '--' repeated prefix indicating their level
     */
    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        repository.findRootCategories(Sort.by("name").ascending()).stream()
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

        SortedSet<Category> sortedChilder = sortSubCategories(category.getChildren());
        sortedChilder
                .forEach(child -> processSubCategories(list, child, level + 1));
    }

    private List<Category> listSubHierarchicalCategories(Category parent, int subLevel) {
        List<Category> hierarchicalCategories = new ArrayList<>();// Create a list to store hierarchical categories for the current branch
        String prefix = "--".repeat(subLevel);// Create a prefix string to indicate the level of indentation

        hierarchicalCategories.add(Category.copyFull(parent, prefix + parent.getName()));// Add the current parent category with the appropriate indentation to the list

        Set<Category> children = sortSubCategories(parent.getChildren()); // Retrieve the children of the parent category
        int newSubLevel = subLevel + 1;// Increment the subLevel to indicate we are going one level deeper in the hierarchy

        children.forEach(subCategory -> {// Process each child category using a forEach loop
            hierarchicalCategories.addAll(listSubHierarchicalCategories(subCategory, newSubLevel));// Add the child category with the updated indentation to the list
        });

        return hierarchicalCategories;// Return the hierarchicalCategories list for the current branch
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children){
        SortedSet<Category> sortedChildren = new TreeSet<>(Comparator.comparing(Category::getName));
        sortedChildren.addAll(children);

        return sortedChildren;
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
                return "DuplicatedName";
            }

            Category categoryByAlias = repository.findByAlias(alias);
            if (categoryByAlias != null && categoryByAlias.getId() != id) {
                return "DuplicatedAlias";
            }

        }

        return "OK";
    }

    public List<Category> listAll(Sort sortDir) {
        Sort sort = Sort.by("name");
        if (sortDir == null || sortDir.isEmpty()) sort = sort.ascending();
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        List<Category> rootCategories = repository.findRootCategories(sort);
        return listHierarchicalCategories(rootCategories);
    }
}
