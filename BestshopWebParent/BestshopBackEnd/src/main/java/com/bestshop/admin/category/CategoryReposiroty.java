package com.bestshop.admin.category;

import com.bestshop.common.entity.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryReposiroty extends JpaRepository<Category, Integer> {

    public Category findAllById(Integer id);

    public Category findByName(String name);

    public Category findByAlias(String alias);

    public Long countById(Integer id);

    @Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
    public List<Category> findRootCategories(Sort sort);

}
