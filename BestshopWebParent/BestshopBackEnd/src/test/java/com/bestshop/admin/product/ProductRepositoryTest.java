package com.bestshop.admin.product;

import com.bestshop.admin.brand.BrandRepository;
import com.bestshop.admin.category.CategoryReposiroty;
import com.bestshop.common.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryReposiroty categoryReposiroty;

    @Autowired
    BrandRepository brandRepository;

    @Test
    @Rollback(value = false)
    void testCreateNewProduct(){
        Product product = new Product();
        product.setName("Dell Inspiron");
        product.setAlias("dell_insipiron");
        product.setShortDescription("Brand New Samsumg Galaxy 256gb.");
        product.setFullDescription("Brand New Samsumg Galaxy 256gb 6.1 screen inch model 2023");
        product.setCreatedTime(LocalDateTime.now());
        product.setUpdatedTime(LocalDateTime.now());
        product.setEnabled(true);
        product.setCost(new BigDecimal("10.0"));
        product.setPrice(new BigDecimal("15.0"));
        product.setLength(1);
        product.setWidth(1);
        product.setHeigth(1);
        product.setWeigth(1);

        product.setCategory(categoryReposiroty.findByName("'Electronics'"));
        product.setBrand(brandRepository.findByName("Samsung"));

        productRepository.save(product);

        assertNotNull(productRepository.findAll());
    }

    @Test
    @Rollback(value = false)
    void testFindByName(){
        String name = "Iphone 11";
        Product product = new Product();
        product.setName(name);
        product.setAlias("iphone_11");
        product.setShortDescription("Brand New Iphone 15 256gb.");
        product.setFullDescription("Brand New Iphone 15 256gb 6.1 screen inch model 2023");
        product.setCreatedTime(LocalDateTime.now());
        product.setUpdatedTime(LocalDateTime.now());
        product.setCost(new BigDecimal("10.0"));
        product.setPrice(new BigDecimal("15.0"));
        product.setLength(1);
        product.setWidth(1);
        product.setHeigth(1);
        product.setWeigth(1);

        product.setCategory(categoryReposiroty.findByName("Iphone"));
        product.setBrand(brandRepository.findByName("Apple"));

        productRepository.save(product);

        Product savedProduct = productRepository.findByName(name);

        assertEquals(name, savedProduct.getName());
    }

    @Test
    public void testListAllProducts(){
        Iterable<Product> productIterable = productRepository.findAll();
        productIterable.forEach(System.out::println);

        assertNotNull(productIterable);
    }

    @Test
    public void testGetProduct(){
        Integer id = 1;
        Product product = productRepository.findById(id).get();
        System.out.println(product);

        assertNotNull(product);
    }

    @Test
    public void testUpdateProduct(){
        Integer id = 1;
        BigDecimal price = new BigDecimal("499.0");
        Product product = productRepository.findById(id).get();
        product.setPrice(price);

        productRepository.save(product);

        assertEquals(price, productRepository.findById(id).get().getPrice());
    }

    @Test
    public void testDeleteProduct(){
        Integer id = 1;
        productRepository.deleteById(id);

        Optional<Product> result = productRepository.findById(id);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testSaveProductWithImages(){
        Product product = productRepository.findById(2).get();
        product.setMainImage("Main Image Test");
        product.addExtraImage("Extra Image 1");
        product.addExtraImage("Extra Image 2");

        productRepository.save(product);

        Product savedProduct = productRepository.findById(2).get();

        assertTrue(savedProduct.getImages().size() > 1);
    }

    @Test
    @Rollback(value = false)
    public void testAddProductDetails(){
        Integer id = 10;
        Product product = productRepository.findById(id).get();

        product.addDetail("Device Memory", "128 Gb");

        assertFalse(product.getDetails().isEmpty());

    }

}