package com.bestshop.admin.category;

import com.bestshop.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CategoryReposiroty extends JpaRepository<Category, Integer> {

    Set<Category> findAllById(Integer id);

}
