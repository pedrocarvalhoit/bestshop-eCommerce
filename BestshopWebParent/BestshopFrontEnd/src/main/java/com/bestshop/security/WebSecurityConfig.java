package com.bestshop.security;

import com.bestshop.security.oauth.CustomerOAuth2UserService;
import com.bestshop.security.oauth.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomerOAuth2UserService oAuth2Service;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginHandler;

    @Autowired
    private DatabaseLoginSuccessHandler databaseLoginHandler;

    @Bean
    UserDetailsService userDetailsService(){

        return new CustomerUserDetailService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authotize -> authotize
                .requestMatchers("/account_details", "/update_account_details",
                        "/cart", "/templates/address_book/**", "/checkout", "/place_order"
                        , "/process_paypal_order").authenticated()
                .anyRequest().permitAll()
                ).formLogin(form -> form.loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(databaseLoginHandler)
                        .permitAll()
                ).oauth2Login(oAuthform -> oAuthform.loginPage("/login")
                        .userInfoEndpoint()
                        .userService(oAuth2Service)
                        .and()
                        .successHandler(oAuth2LoginHandler)
                ).logout(LogoutConfigurer::permitAll)
                .rememberMe(remember -> remember.key("abcdefghijklmnopq_1234567890").tokenValiditySeconds(7 * 24 * 60 * 60))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);


        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**", "/webfonts/**", "**.css"));
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder encoder,
                                                       CustomerUserDetailService userDetailsService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(encoder)
                .and()
                .build();
    }

}
