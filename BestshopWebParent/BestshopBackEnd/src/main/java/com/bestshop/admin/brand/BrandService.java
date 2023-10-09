package com.bestshop.admin.brand;

import com.bestshop.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BrandService {

    @Autowired
    private BrandRepository repository;

    public static final int NUMBER_ITEM_PER_PAGE = 10;

    public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyword){
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_ITEM_PER_PAGE, sort);

        if (keyword != null){
            return repository.findAll(keyword, pageable);
        }

        return repository.findAll(pageable);
    }

    public List<Brand> listAll(Sort sort) {
        return repository.findAll(sort);
    }

    public Brand save(Brand brand) {
        return repository.save(brand);
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
