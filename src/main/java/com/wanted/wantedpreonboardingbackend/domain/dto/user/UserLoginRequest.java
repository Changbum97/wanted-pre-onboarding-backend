package com.wanted.wantedpreonboardingbackend.domain.dto.user;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {

    private String email;
    private String password;
}
