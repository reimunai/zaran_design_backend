package com.example.zaran_design_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 认证接口全部放行
                        .requestMatchers("/api/auth/**").permitAll()
                        // 用户公开接口放行
                        .requestMatchers("/api/users/{userId}").permitAll()
                        .requestMatchers("/api/users/{userId}/patterns").permitAll()
                        .requestMatchers("/api/users/{userId}/sketches").permitAll()
                        // 作品广场公开
                        .requestMatchers("/api/patterns/square/**").permitAll()
                        .requestMatchers("/api/patterns/{patternId}").permitAll()
                        .requestMatchers("/api/patterns/{patternId}/comments").permitAll()
                        // 知识库公开
                        .requestMatchers("/api/knowledge/categories").permitAll()
                        .requestMatchers("/api/knowledge/entries/**").permitAll()
                        .requestMatchers("/api/knowledge/search").permitAll()
                        .requestMatchers("/api/knowledge/terminology/**").permitAll()
                        // 其他接口需要认证
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
