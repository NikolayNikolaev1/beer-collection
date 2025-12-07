package com.nnikolaev.beercollection.config;


import com.nnikolaev.beercollection.security.JwtAuthenticationFilter;
import com.nnikolaev.beercollection.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static com.nnikolaev.beercollection.common.Constant.Route;

@Configuration
public class SecurityConfig {
    @Autowired
    private CustomUserDetailsService userDetailsService; // your CustomUserDetailsService
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; // your custom filter

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // since this is a REST API with JWT, disable CSRF (no sessions / cookies)
                .csrf(AbstractHttpConfigurer::disable)
                // make session stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // authorize requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(Route.AUTH_PREFIX + "/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                // register the authentication provider
                .authenticationProvider(authenticationProvider())
                // add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(this.userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
