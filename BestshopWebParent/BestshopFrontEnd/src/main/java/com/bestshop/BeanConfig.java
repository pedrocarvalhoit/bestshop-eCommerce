package com.bestshop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class BeanConfig {


    @Bean
    SecureRandom secureRandom(){
        return new SecureRandom();
    }
}
