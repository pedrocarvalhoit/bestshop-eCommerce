package com.bestshop.customer;

import com.bestshop.common.entity.AuthenticationType;
import com.bestshop.common.entity.Country;
import com.bestshop.common.entity.Customer;
import com.bestshop.setting.CountryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private CountryRepository countryRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<Country> listAllCountries() {
        return countryRepo.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email) {
        Customer customer = customerRepo.findByEmail(email);
        return customer == null;
    }

    public void registerCustomer(Customer customer){
        passwordEncoder(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(AuthenticationType.DATABASE);

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[45];
        random.nextBytes(bytes);

        String randomCode = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        customer.setVerificationCode(randomCode);

        customerRepo.save(customer);
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepo.findByEmail(email);
    }

    private void passwordEncoder(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }

    public boolean verify(String verificationCode) {
        Customer customer = customerRepo.findByVerificationCode(verificationCode);

        if (customer == null || customer.isEnabled()) {
            return false;
        } else {
            customerRepo.enable(customer.getId());
            return true;
        }
    }

    public void updateAuthenticationType(Customer customer, AuthenticationType type){
        if (!customer.getAuthenticationType().equals(type)){
            customerRepo.updateAuthenticationType(customer.getId(), type);
        }
    }

    public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode,
                                             AuthenticationType authenticationType){
        Customer customer = new Customer();
        customer.setEmail(email);
        setName(name, customer);

        customer.setEnabled(true);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(authenticationType);
        customer.setPassword("");
        customer.setAddressLine1("");
        customer.setCity("");
        customer.setState("");
        customer.setPhoneNumber("");
        customer.setPostalCode("");
        customer.setCountry(countryRepo.findByCode(countryCode));

        customerRepo.save(customer);
    }

    private void setName(String name, Customer customer) {
        String[] names = name.split(" ");
        if (names.length >= 2){
            customer.setFirstName(names[0]);
            customer.setLastName(names[1]);
        } else if (names.length == 1) {
            customer.setFirstName(names[0]);
            customer.setLastName("");
        }
    }

}