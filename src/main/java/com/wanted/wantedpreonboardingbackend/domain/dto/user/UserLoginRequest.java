package com.wanted.wantedpreonboardingbackend.domain.dto.user;

import lombok.*;

@Getter
@Setter
public class UserLoginRequest {

    private String email;
    private String password;
}
