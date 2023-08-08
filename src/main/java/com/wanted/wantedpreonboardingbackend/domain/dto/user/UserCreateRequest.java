package com.wanted.wantedpreonboardingbackend.domain.dto.user;

import com.wanted.wantedpreonboardingbackend.domain.entity.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    private String email;
    private String password;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(this.email)
                .password(encodedPassword)
                .build();
    }
}
