package com.bestshop.admin.user;

import com.bestshop.admin.user.repository.RoleRespository;
import com.bestshop.common.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToObject;
import static org.hamcrest.Matchers.greaterThan;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRespositoryTest {

    @Autowired
    private RoleRespository respository;

    @Test
    @DisplayName("Test Create First Role")
    public void testCreateFirstRole(){
        Role roleAdmin = new Role("test", "manage everything");
        Role savedRole = respository.save(roleAdmin);

        String name = savedRole.getName();

        assertThat(savedRole.getId(), greaterThan(0));
        assertThat(savedRole.getName(), equalToObject(name));
    }

    @Test
    public void testCreateRestRoles() {
        Role roleSalesperson = new Role("Salesperson", "manage product price, "
                + "customers, shipping, orders and sales report");

        Role roleEditor = new Role("Editor", "manage categories, brands, "
                + "products, articles and menus");

        Role roleShipper = new Role("Shipper", "view products, view orders "
                + "and update order status");

        Role roleAssistant = new Role("Assistant", "manage questions and reviews");

        respository.saveAll(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));
    }
}