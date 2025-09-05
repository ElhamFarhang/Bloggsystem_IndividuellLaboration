package com.example.bloggsystem_individuelllaboration.configs;

import com.example.bloggsystem_individuelllaboration.converters.JwtAuthConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final  JwtAuthConverter jwtAuthConverter;

    @Autowired
    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                     auth
                        .requestMatchers("/api/v2/posts", "/api/v2/post/{id}").authenticated()
                        .requestMatchers("/api/v2/newpost").hasRole("bloggUser")
                        .requestMatchers("/api/v2/updatepost").hasAnyRole("bloggUser", "bloggAdmin")
                        .requestMatchers("/api/v2/deletepost/{id}").hasAnyRole("bloggUser", "bloggAdmin")
                        .requestMatchers("/api/v2/count").hasRole("bloggAdmin")
                        .requestMatchers("/api/v2/test").hasAnyRole("bloggUser", "bloggAdmin")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                     oauth2
                             .jwt(jwt->jwt.jwtAuthenticationConverter(jwtAuthConverter))
                );
        return http.build();
    }
}
