package com.firstone.greenjangteo.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint) // 인증되지 않았을 시 custom entry point 사용
                .and()
                .httpBasic().disable() // JWT 사용
                .csrf().disable() // 프론트엔드 연동 시 활성화
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .antMatchers("/", "/swagger-ui/**", "/v2/api-docs", "/swagger-resources/**",
                        "/**/signup", "/**/login", "/users")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/users/{userId}").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter
                                (jwtTokenProvider, customAuthenticationEntryPoint),
                        UsernamePasswordAuthenticationFilter.class)
                .logout().permitAll();

        return http.build();
    }
}
