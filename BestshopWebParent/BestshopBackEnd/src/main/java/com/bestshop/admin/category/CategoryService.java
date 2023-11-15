package com.bestshop.admin.category;

import com.bestshop.common.exception.CategoryNotFoundException;
import com.bestshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Transactional
public class CategoryService {

    public static final int ROOT_CATEGORIES_PER_PAGE = 4;

    @Autowired
    private CategoryReposiroty repository;

    public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir,
                                     String keyword) {
        Sort sort = Sort.by("name");

        if (sortDir.equals("asc")) {
            sort = sort.ascending();
        } else if (sortDir.equals("desc")) {
            sort = sort.descending();
        }

        Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

        Page<Category> pageCategories = null;

        if (keyword != null && !keyword.isEmpty()) {
            pageCategories = repository.search(keyword, pageable);
        } else {
            pageCategories = repository.findRootCategories(pageable);
        }

        List<Category> rootCategories = pageCategories.getContent();

        pageInfo.setTotalElements(pageCategories.getTotalElements());
        pageInfo.setTotalPages(pageCategories.getTotalPages());

        if (keyword != null && !keyword.isEmpty()) {
            List<Category> searchResult = pageCategories.getContent();
            for (Category category : searchResult) {
                category.setHasChildren(category.getChildren().size() > 0);
            }

            return searchResult;

        } else {
            return listHierarchicalCategories(rootCategories, sortDir);
        }
    }

    public Category save(Category category) {
        Category parent = category.getParent();
        if (parent != null) {
            String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
            allParentIds += String.valueOf(parent.getId()) + "-";
            category.setAllParentIDs(allParentIds);
        }


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

    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory, name));

                listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
            }
        }

        return hierarchicalCategories;
    }

    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();

        Iterable<Category> categoriesInDB = repository.findRootCategories(Sort.by("name").ascending());

        for (Category category : categoriesInDB) {
            categoriesUsedInForm.add(Category.copyIdAndName(category));

            Set<Category> children = sortSubCategories(category.getChildren());

            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();
                categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

                listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
            }
        }

        return categoriesUsedInForm;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
                                               Category parent, int subLevel, String sortDir) {
        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
        int newSubLevel = subLevel + 1;

        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();

            hierarchicalCategories.add(Category.copyFull(subCategory, name));

            listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
        }

    }

    private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm,
                                             Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = sortSubCategories(parent.getChildren());

        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();

            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

            listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
        }
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }

    public boolean checkExistingCategory(Category category) {
        if (category.getId() == null) {
            return false;
        }
        return repository.findById(category.getId()).isPresent();
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                if (sortDir.equals("asc")) {
                    return cat1.getName().compareTo(cat2.getName());
                } else {
                    return cat2.getName().compareTo(cat1.getName());
                }
            }
        });

        sortedChildren.addAll(children);

        return sortedChildren;
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
        return listHierarchicalCategories(rootCategories, String.valueOf(sortDir));
    }


}