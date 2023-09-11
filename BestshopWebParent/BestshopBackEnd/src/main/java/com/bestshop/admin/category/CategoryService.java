package com.bestshop.admin.category;

import com.bestshop.common.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
public class CategoryService {

    @Autowired
    CategoryReposiroty reposiroty;

    public List<Category> listAll(){
        return reposiroty.findAll();
    }

}
