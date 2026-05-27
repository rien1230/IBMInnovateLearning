package com.example.project_code_2.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Define the PasswordEncoder as a Bean (BCrypt for encoding passwords)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure HttpSecurity with SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // Restrict access to the user profile update page to authenticated users only
                .requestMatchers("/profile/update").authenticated()  // Only authenticated users can access the update profile page
                // Allow other pages to be accessed without authentication
                .anyRequest().permitAll()  // All other pages can be accessed without authentication
                .and().formLogin().disable()  // Disable form login (not needed for this functionality)
                .httpBasic().disable()  // Disable basic authentication
                .logout().disable(); // Disable logout (optional)

        return http.build();
    }
}


