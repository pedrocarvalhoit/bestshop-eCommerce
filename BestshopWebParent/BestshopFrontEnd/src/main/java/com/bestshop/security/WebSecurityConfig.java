package com.bestshop.security;

import com.bestshop.security.oauth.CsutomerOAuth2UserService;
import com.bestshop.security.oauth.CustomerOAuth2User;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private CsutomerOAuth2UserService oAuth2Service;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(){
        return new CustomerUserDetailService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authotize) -> authotize
                .requestMatchers("/customer").authenticated()
                .anyRequest().permitAll()
                ).formLogin((form) -> form.loginPage("/login")
                        .usernameParameter("email")
                        .permitAll()
                ).oauth2Login((oAuthform) -> oAuthform.loginPage("/login")
                        .userInfoEndpoint()
                        .userService(oAuth2Service))
                .logout(LogoutConfigurer::permitAll)
                .rememberMe((remember) -> remember.key("abcdefghijklmnopq_1234567890").tokenValiditySeconds(7 * 24 * 60 * 60));


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
