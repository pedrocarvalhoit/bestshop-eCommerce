package com.bestshop.shipping;

import com.bestshop.common.entity.Country;
import com.bestshop.common.entity.ShippingRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShippingRateRepositoryTests {

    @Autowired
    private ShippingRateRepository repo;

    @Test
    public void testFindByCountryAndState() {
        Country usa = new Country(106);
        String state = "West Bengal";
        ShippingRate shippingRate = repo.findByCountryAndState(usa, state);

        org.assertj.core.api.Assertions.assertThat(shippingRate).isNotNull();
        System.out.println(shippingRate);
    }
}