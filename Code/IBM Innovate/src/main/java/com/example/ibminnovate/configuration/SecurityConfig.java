package com.example.ibminnovate.configuration;

import com.example.ibminnovate.handler.CustomOAuth2SuccessHandler;
import com.example.ibminnovate.inter.JwtTokenFilter;
import com.example.ibminnovate.service.CustomOAuth2UserService;
import com.example.ibminnovate.service.CustomUserDetailsService;
import com.example.ibminnovate.utility.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService oAuth2UserService;
    private final JwtUtil jwtUtil;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;


    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          CustomOAuth2UserService oAuth2UserService,
                          JwtUtil jwtUtil,
                          CustomOAuth2SuccessHandler customOAuth2SuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.oAuth2UserService = oAuth2UserService;
        this.jwtUtil = jwtUtil;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/", "/password", "/send-otp", "/validate-otp").permitAll()
                        .requestMatchers("/profile/**", "/update", "/update-profile", "/update-password").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService)) // Use custom OAuth2 service
                        .successHandler(customOAuth2SuccessHandler) // Use custom success handler
                )
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .logout(logout -> logout.disable())
                .addFilterBefore(new JwtTokenFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class); // Add JWT filter

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }



}