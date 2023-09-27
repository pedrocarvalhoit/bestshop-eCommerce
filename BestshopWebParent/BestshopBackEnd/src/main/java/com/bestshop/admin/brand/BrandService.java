package com.bestshop.admin.brand;

import com.bestshop.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandRepository repository;

    public Brand save(Brand brand) {
        return repository.save(brand);
    }

    public List<Brand> findAll() {
        return repository.findAll();
    }

    public List<Brand> listAll(Sort sort) {
        return repository.findAll(sort);
    }

    public Brand findById(Integer id) {
        return repository.findById(id).get();
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
