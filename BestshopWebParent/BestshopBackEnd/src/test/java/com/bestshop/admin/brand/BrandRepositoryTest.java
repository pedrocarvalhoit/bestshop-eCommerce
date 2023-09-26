package com.bestshop.admin.brand;

import com.bestshop.admin.category.repository.CategoryReposiroty;
import com.bestshop.common.entity.Brand;
import com.bestshop.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BrandRepositoryTest {

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    CategoryReposiroty categoryRepository;

    @Test
    public void createNewBrands(){
        Brand brand = new Brand("Asics", "Acer.png");
        Brand brand2 = new Brand("Nike", "Apple.png");
        Brand brand3 = new Brand("Adidas", "Samsumg.png");

        Set<Category> brand2Categories = new HashSet<>();
        brand2Categories.add(categoryRepository.findByName("Cell Phones & Accessories"));
        brand2Categories.add(categoryRepository.findByName("Tablets"));

        brand2.setCategories(brand2Categories);

        Set<Category> brand3Categories = new HashSet<>();
        brand3Categories.add(categoryRepository.findByName("Memory"));
        brand3Categories.add(categoryRepository.findByName("Internal Hard Drives"));

        brand3.setCategories(brand3Categories);

        brandRepository.save(brand);
        brandRepository.save(brand2);
        brandRepository.save(brand3);

        assertTrue(brandRepository.findAll().size() > 0);
        assertTrue(brandRepository.findAll().contains(brand));
    }

    @Test
    public void findAllBrands(){
        List<Brand> listBrands = brandRepository.findAll();
        listBrands.forEach(System.out::println);

        assertNotNull(listBrands);
    }
    
    @Test
    public void testFindById(){
        Brand brand = brandRepository.findById(203).get();
        assertEquals(brand.getName(), "Apple");
    }

    @Test
    public void testUpdateABrand(){
        Brand brandToUpdate = brandRepository.findByName("Samsumg Eletronics");

        String newName = "Samsumg 123123";

        brandToUpdate.setName(newName);
        brandRepository.save(brandToUpdate);

        assertNotNull(brandRepository.findByName(newName));
    }

    @Test
    public void testDelete(){
        brandRepository.deleteById(203);

        Optional<Brand> brand = brandRepository.findById(203);

        assertTrue(brand.isEmpty());
    }

}