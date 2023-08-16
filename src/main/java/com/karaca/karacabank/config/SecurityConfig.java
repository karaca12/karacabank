package com.karaca.karacabank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf()
                .disable()
                .authorizeHttpRequests((authorize)-> authorize
                        .requestMatchers("/customers/**").hasAnyRole("ADMIN","MANAGER")
                        .requestMatchers("/transactions/**").hasRole("ADMIN")
                        .requestMatchers("/accounts/**").hasAnyRole("ADMIN","MANAGER")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails admin= User
                .builder()
                .username("admin")
                .password(passwordEncoder()
                        .encode("admin"))
                .roles("ADMIN")
                .build();

        UserDetails manager= User
                .builder()
                .username("manager")
                .password(passwordEncoder()
                        .encode("manager"))
                .roles("MANAGER")
                .build();
        return new InMemoryUserDetailsManager(admin,manager);
    }
}
