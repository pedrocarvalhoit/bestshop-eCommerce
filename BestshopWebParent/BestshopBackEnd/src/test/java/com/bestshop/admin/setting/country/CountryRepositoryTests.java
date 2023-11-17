package com.bestshop.admin.setting.country;

import com.bestshop.common.entity.Country;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class CountryRepositoryTests {

    @Autowired
    private CountryRepository repo;

    @Test
    void testCreateCountry() {
        Country country = repo.save(new Country("China", "CN"));

    }

    @Test
    void testListCountries() {
        List<Country> listCountries = repo.findAllByOrderByNameAsc();
        listCountries.forEach(System.out::println);

        Assertions.assertThat(listCountries.size()).isGreaterThan(0);
    }

    @Test
    void testUpdateCountry() {
        Integer id = 1;
        String name = "Republic of India";

        Country country = repo.findById(id).get();
        country.setName(name);

        Country updatedCountry = repo.save(country);

        Assertions.assertThat(updatedCountry.getName()).isEqualTo(name);
    }

    @Test
    void testGetCountry() {
        Integer id = 2;
        Country country = repo.findById(id).get();
        Assertions.assertThat(country).isNotNull();
    }

    @Test
    void testDeleteCountry() {
        Integer id = 5;
        repo.deleteById(id);

        Optional<Country> findById = repo.findById(id);
        Assertions.assertThat(findById.isEmpty());
    }
}