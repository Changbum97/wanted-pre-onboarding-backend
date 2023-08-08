package com.wanted.wantedpreonboardingbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@SpringBootApplication
public class WantedPreOnboardingBackendApplication {

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer customizer() {
        return p -> {
            // Page를 1페이지 부터 시작하도록 설정
            p.setOneIndexedParameters(true);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(WantedPreOnboardingBackendApplication.class, args);
    }

}
