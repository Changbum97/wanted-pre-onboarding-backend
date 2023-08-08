package com.wanted.wantedpreonboardingbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.wantedpreonboardingbackend.domain.Response;
import com.wanted.wantedpreonboardingbackend.exception.CustomException;
import com.wanted.wantedpreonboardingbackend.exception.ErrorCode;
import com.wanted.wantedpreonboardingbackend.exception.ErrorResponse;
import com.wanted.wantedpreonboardingbackend.filter.JwtTokenFilter;
import com.wanted.wantedpreonboardingbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    @Value("${jwt.token.secret}")
    private String secretKey;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/boards").authenticated()
                .anyRequest().permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // UserNamePasswordAuthenticationFilter을 적용 하기 전에 JwtTokenFilter를 적용
                .addFilterBefore(new JwtTokenFilter(userRepository, secretKey), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                // Security Filter Chain에서 발생하는 Exception은 ExceptionManager 까지 가지 않기 때문에 여기서 직접 처리
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        ErrorCode errorCode = ErrorCode.INVALID_PERMISSION;
                        response.setCharacterEncoding("UTF-8");
                        response.setStatus(errorCode.getStatus().value());
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.writeValue(response.getWriter(), Response.error(new CustomException(errorCode)).getBody());
                    }
                })
                .and()
                .build();
    }

}
