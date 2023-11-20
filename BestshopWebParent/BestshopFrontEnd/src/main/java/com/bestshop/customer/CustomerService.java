package com.bestshop.customer;

import com.bestshop.common.entity.Country;
import com.bestshop.common.entity.Customer;
import com.bestshop.setting.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CountryRepository countryRepo;
    @Autowired private CustomerRepository customerRepo;

    public List<Country> listAllCountries() {
        return countryRepo.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email) {
        Customer customer = customerRepo.findByEmail(email);
        return customer == null;
    }
}