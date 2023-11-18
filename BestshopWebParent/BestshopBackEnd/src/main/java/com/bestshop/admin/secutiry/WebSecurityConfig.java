package com.bestshop.admin.secutiry;

import com.bestshop.common.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new BestShopUserDetailsService();
    }

    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/users/**", "/settings", "/countries/**", "/states/**").hasAuthority("Admin")
                        .requestMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")

                        .requestMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")

                        .requestMatchers("/products/edit/**", "/products/save", "/products/check_unique")
                            .hasAnyAuthority("Admin", "Editor", "Salesperson")

                        .requestMatchers("/products", "/products/", "/products/detail/**", "/products/page/**")
                            .hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")

                        .requestMatchers("/products/**").hasAnyAuthority("Admin", "Editor")

                        .requestMatchers("/questions/**", "/reviews/**").hasAnyAuthority("Admin", "Assistant")
                        .requestMatchers("/customers/**", "/shipping/**").hasAnyAuthority("Admin", "Salesperson")
                        .requestMatchers("/orders/**").hasAnyAuthority("Admin", "Salesperson", "Shipper")
                        .requestMatchers("/reports/**").hasAnyAuthority("Admin", "Salesperson")
                        .requestMatchers("/articles/**", "/menus/**").hasAnyAuthority("Admin", "Editor")
                        .anyRequest().authenticated()
                ).formLogin((form) -> form.loginPage("/login")
                        .usernameParameter("email")
                        .permitAll()
                ).logout(LogoutConfigurer::permitAll)
                .rememberMe((remember) -> remember.key("abcdefghijklmnopq_1234567890").tokenValiditySeconds(7 * 24 * 60 * 60));


        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**", "/webfonts/**", "**.css");
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder encoder,
                                                       BestShopUserDetailsService userDetailsService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(encoder)
                .and()
                .build();
    }


}
