package com.wanted.wantedpreonboardingbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class WantedPreOnboardingBackendApplication {

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer customizer() {
        return p -> {
            // Page를 1페이지 부터 시작하도록 설정
            p.setOneIndexedParameters(true);
        };
    }

    @PostConstruct
    public void setTimeZone() {
        // 프로젝트의 TimeZone을 서울로 맞춰줌
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(WantedPreOnboardingBackendApplication.class, args);
    }

}
