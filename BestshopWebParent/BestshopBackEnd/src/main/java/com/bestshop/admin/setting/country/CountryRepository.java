package com.bestshop.admin.setting.country;

import com.bestshop.common.entity.Country;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CountryRepository extends CrudRepository<Country, Integer> {
    public List<Country> findAllByOrderByNameAsc();

    Country findByName(String china);
}