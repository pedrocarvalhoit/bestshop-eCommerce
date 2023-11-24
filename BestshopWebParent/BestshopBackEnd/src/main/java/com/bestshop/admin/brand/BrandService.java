package com.bestshop.admin.brand;

import com.bestshop.admin.paging.PagingAndSortingHelper;
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
    private BrandRepository repo;

    public static final int BRANDS_PER_PAGE = 10;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, BRANDS_PER_PAGE, repo);
    }

    public List<Brand> listAll() {
        return (List<Brand>) repo.findAll();
    }

    public Brand save(Brand brand) {
        return repo.save(brand);
    }

    public Brand get(Integer id) throws BrandNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new BrandNotFoundException("Could not find any brand with ID " + id);
        }
    }

    public void delete(Integer id) throws BrandNotFoundException {
        Long countById = repo.countByid(id);

        if (countById == null || countById == 0) {
            throw new BrandNotFoundException("Could not find any brand whith ID " + id);
        }

        repo.deleteById(id);
    }

    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Brand brand = repo.findByName(name);

        if (isCreatingNew) {
            if (brand != null) return "Duplicate";
        } else {
            if (brand != null && brand.getId() != id) {
                return "Duplicate";
            }
        }

        return "OK";
    }
}
