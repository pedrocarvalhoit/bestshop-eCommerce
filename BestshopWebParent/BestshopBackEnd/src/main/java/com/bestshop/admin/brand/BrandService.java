package com.bestshop.admin.brand;

import com.bestshop.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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


    public Brand get(Integer id) throws BrandNotFoundException {
        try {
            return repository.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new BrandNotFoundException("Could not find any brand with ID " + id);
        }
    }

    public void delete(Integer id) throws BrandNotFoundException {
        Long countById = repository.countByid(id);

        if(countById == null || countById == 0){
            throw new BrandNotFoundException("Could not find any brand whith ID " + id);
        }

        repository.deleteById(id);
    }

    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Brand brand = repository.findByName(name);

        if (isCreatingNew){
            if (brand != null)return "Duplicate";
        }else {
            if (brand != null && brand.getId() != id){
                return "Duplicate";
            }
        }

        return "OK";
    }
}
