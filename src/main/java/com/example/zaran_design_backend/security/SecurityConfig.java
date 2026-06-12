package com.example.zaran_design_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                        // 用户模块：需要认证的特定路径
                        .requestMatchers("/api/users/profile").authenticated()
                        .requestMatchers("/api/users/password").authenticated()
                        .requestMatchers("/api/users/apply-designer").authenticated()
                        .requestMatchers("/api/users/applications/**").authenticated()
                        // 用户公开主页（GET /api/users/{userId}）
                        .requestMatchers(HttpMethod.GET, "/api/users/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/*/patterns").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/*/sketches").permitAll()
                        // 用户模块其他操作（关注/取消关注等）需要认证
                        .requestMatchers("/api/users/**").authenticated()
                        // 草图模块：分类树公开，其余需认证
                        .requestMatchers(HttpMethod.GET, "/api/sketches/categories").permitAll()
                        .requestMatchers("/api/sketches/**").authenticated()
                        // 作品广场公开（无需登录）
                        .requestMatchers("/api/patterns/square/**").permitAll()
                        // 作品详情、评论列表、点赞 公开（可选认证，控制器内部做个性化判断）
                        .requestMatchers(HttpMethod.GET, "/api/patterns/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/patterns/*/comments").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/patterns/*/like").permitAll()
                        // 作品模块其他接口需要认证
                        .requestMatchers("/api/patterns/**").authenticated()
                        // 知识库公开
                        .requestMatchers("/api/knowledge/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/knowledge/entries/**").permitAll()
                        .requestMatchers("/api/knowledge/search").permitAll()
                        .requestMatchers("/api/knowledge/terminology/**").permitAll()
                        .requestMatchers("/api/knowledge/**").authenticated()
                        // 协同编辑模块（全部需要认证，控制器/WebSocket内部做参与者权限校验）
                        .requestMatchers("/api/collab/**").authenticated()
                        // WebSocket 协同编辑端点（由 Handler 自行校验 token 参数）
                        .requestMatchers("/ws/collab/**").permitAll()
                        // 生成队列状态公开
                        .requestMatchers(HttpMethod.GET, "/api/generation/queue/status").permitAll()
                        // 系统管理模块（全部需要认证，控制器内部校验 admin 角色）
                        .requestMatchers("/api/admin/**").authenticated()
                        // 其他接口需要认证
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
