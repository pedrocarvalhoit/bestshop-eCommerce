package com.bestshop.adress;

import com.bestshop.common.entity.Address;
import com.bestshop.common.entity.Country;
import com.bestshop.common.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class AddressRepositoryTests {

    @Autowired
    private AddressRepository repo;

    @Test
    public void testAddNew() {
        Integer customerId = 5;
        Integer countryId = 234; // USA

        Address newAddress = new Address();
        newAddress.setFirstName("Tobie");
        newAddress.setLastName("Abel");
        newAddress.setPhoneNumber("19094644165");
        newAddress.setAddressLine1("4213 Gordon Street");
        newAddress.setAddressLine2("Novak Building");
        newAddress.setCity("Chino");
        newAddress.setState("California");
        newAddress.setPostalCode("91710");

        Address savedAddress = repo.save(newAddress);

        org.assertj.core.api.Assertions.assertThat(savedAddress).isNotNull();
        org.assertj.core.api.Assertions.assertThat(savedAddress.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        Integer customerId = 5;
        List<Address> listAddresses = repo.findByCustomer(new Customer(customerId));
        org.assertj.core.api.Assertions.assertThat(listAddresses.size()).isGreaterThan(0);

        listAddresses.forEach(System.out::println);
    }

    @Test
    public void testFindByIdAndCustomer() {
        Integer addressId = 1;
        Integer customerId = 5;

        Address address = repo.findByIdAndCustomer(addressId, customerId);

        org.assertj.core.api.Assertions.assertThat(address).isNotNull();
        System.out.println(address);
    }

    @Test
    public void testUpdate() {
        Integer addressId = 1;
        String phoneNumber = "646-232-3932";

        Address address = repo.findById(addressId).get();
        address.setPhoneNumber(phoneNumber);

        Address updatedAddress = repo.save(address);
        org.assertj.core.api.Assertions.assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    public void testDeleteByIdAndCustomer() {
        Integer addressId = 1;
        Integer customerId = 5;

        repo.deleteByIdAndCustomer(addressId, customerId);

        Address address = repo.findByIdAndCustomer(addressId, customerId);
        org.assertj.core.api.Assertions.assertThat(address).isNull();
    }


    @Test
    public void testSetDefault() {
        Integer addressId = 8;
        repo.setDefaultAddress(addressId);

        Address address = repo.findById(addressId).get();
        org.assertj.core.api.Assertions.assertThat(address.isDefaultForShipping()).isTrue();
    }

    @Test
    public void testSetNonDefaultAddresses() {
        Integer addressId = 8;
        Integer customerId = 5;
        repo.setNonDefaultForOthers(addressId, customerId);
    }
}