package com.bestshop.admin.secutiry;

import com.bestshop.admin.user.UserRepository;
import com.bestshop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class BestShopUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.getUserByEmail(email);
        if(user != null){
            return new BestshopUserDetails(user);
        }
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
