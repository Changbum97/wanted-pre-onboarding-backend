package com.wanted.wantedpreonboardingbackend.domain.dto.user;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse {

    private String email;
    private String jwtToken;
}
