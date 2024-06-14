package com.example.ristorante;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @SuppressWarnings("deprecation")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests -> 
                authorizeRequests
                    .requestMatchers("/", "/home").permitAll() 
                    .anyRequest().authenticated()
            )
            .oauth2Login(oauth2Login -> 
    oauth2Login
        .loginPage("/oauth2/authorization/google")
            )
        .logout(logout -> 
        logout
            .logoutSuccessUrl("/") 
            .permitAll()
    );     
         

        return http.build();
    }
}