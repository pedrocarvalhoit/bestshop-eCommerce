package com.bestshop.admin.brand;

import com.bestshop.common.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand,Integer> {

    Brand findByName(String name);
}
