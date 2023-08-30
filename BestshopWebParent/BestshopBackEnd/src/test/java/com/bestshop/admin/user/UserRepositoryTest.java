package com.bestshop.admin.user;

import com.bestshop.common.entity.Role;
import com.bestshop.common.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    UserRepository repository;

    @Autowired
    RoleRespository roleRespository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("Test Create User With One Role")
    public void testCreateUserWithOneRole(){
        Role roleAdmin = testEntityManager.find(Role.class, 1);
        User user = new User("pedro@aqwrwqrqwret", "pedro2020", "pedro", "carvalho");
        user.addRole(roleAdmin);

        User savedUser = repository.save(user);
        assertEquals(savedUser.getFirstName(), user.getFirstName());
        assertTrue(savedUser.getId() > 0);
    }

    @Test
    @DisplayName("Test Create User With Two Roles")
    public void testCreateUserWithTwoRoles(){
        User user = new User("paulo@andre.com", "paulo2020", "Paulo", "duarte");
        user.addRole(new Role(1));
        user.addRole(new Role(5));

        User savedUser = repository.save(user);

        assertTrue(savedUser.getId() > 0);
    }

    @Test
    public void testListAllUSers(){
        Iterable<User> userList = repository.findAll();
        userList.forEach(System.out::println);

        assertNotNull(userList);
        assertFalse(((List<User>)userList).isEmpty());
    }

    @Test
    public void testGetUserById(){
        User user = repository.findById(1).orElse(null);
        assertNotNull(user);
    }

    @Test
    @Rollback(value = false)
    public void testUpdateUserDetails(){
        User user = repository.findById(1).orElse(null);
        assert user != null;

        user.setEnabled(true);
        user.setEmail("pedroduarte@pedro.com");

        repository.save(user);
    }

    @Test
    @Rollback(value = false)
    public void testUpdateUserRoles(){
        User user = repository.findById(5).orElse(null);
        assert user != null;

        Role editor = roleRespository.findById(2).orElse(null);
        Role shipper = roleRespository.findById(1).orElse(null);

        user.getRoles().remove(shipper);
        user.addRole(editor);

        repository.save(user);
    }

    @Test
    public void testDeleteUser(){
        Integer id = 5;
        repository.deleteById(id);
        Optional<User> deletedUser = repository.findById(id);

        assertTrue(deletedUser.isEmpty());
    }

    @Test
    public void testGetUserByEmail(){
        User user = repository.getUserByEmail("pedroduarte@pedro.com");
        System.out.println(user);

        assertNotNull(user);
        assertEquals(user.getId(), 1);
    }

    @Test
    void testCountById() {
        Integer id = 1;
        Long  countByid = repository.countById(id);

        assertTrue(countByid > 0);
    }

    @Test
    @Rollback(value = false)
    void updateDisableStatus() {
        Integer id = 107;
        repository.updateEnabledStatus(id, false);
    }

    @Test
    @Rollback(value = false)
    void updateEnableStatus() {
        Integer id = 107;
        repository.updateEnabledStatus(id, true);
    }

    @Test
    public void testListFirstPage(){
        int pageNumber = 0;
        int pageSize = 4;

        Pageable pageable = PageRequest.of(0,4);
        Page<User> page = repository.findAll(pageable);

        List<User> listUsers = page.getContent();
        listUsers.forEach((System.out::println));

        assertEquals(listUsers.size(), pageSize);
    }

    @Test
    public void testSearchUser() {
        String keyword = "bruce";
        Pageable pageable = PageRequest.of(0, 4);

        Page<User> resultList = repository.findAll(keyword, pageable);
        resultList.forEach(System.out::println);

    }
}