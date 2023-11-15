package com.bestshop.category;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bestshop.common.entity.Category;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE c.enabled = true ORDER BY c.name ASC")
    public List<Category> findAllEnabled();
}
