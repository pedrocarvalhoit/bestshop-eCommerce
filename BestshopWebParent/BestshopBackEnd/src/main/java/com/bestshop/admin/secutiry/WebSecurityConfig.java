package com.bestshop.admin.secutiry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder PasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                (authorize) -> authorize
                        .anyRequest().permitAll()
                );
//                .formLogin()
//                .loginPage("/login")
//                .defaultSuccessUrl("/", true)
//                .failureUrl("/login-error")
//                .permitAll()
//                .and()
//                .logout()
//                .logoutSuccessUrl("/")
//                .deleteCookies("JSESSIONID")
//                .and()
//                .exceptionHandling()
//                .accessDeniedPage("/negado");

        return http.build();
    }
}
