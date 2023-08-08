package com.wanted.wantedpreonboardingbackend.service;

import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserCreateRequest;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserDto;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserLoginRequest;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserLoginResponse;
import com.wanted.wantedpreonboardingbackend.domain.entity.User;
import com.wanted.wantedpreonboardingbackend.exception.CustomException;
import com.wanted.wantedpreonboardingbackend.exception.ErrorCode;
import com.wanted.wantedpreonboardingbackend.repository.UserRepository;
import com.wanted.wantedpreonboardingbackend.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    @Value("${jwt.token.secret}")
    private String secretKey;

    @Value("${jwt.token.access-token-duration}")
    private Long accessTokenDurationSec;

    public UserDto save(UserCreateRequest req) {
        validation(req.getEmail(), req.getPassword());

        String encodedPassword = encoder.encode(req.getPassword());
        User savedUser = userRepository.save( req.toEntity(encodedPassword) );
        return UserDto.of(savedUser);
    }

    public UserLoginResponse login(UserLoginRequest req) {
        validation(req.getEmail(), req.getPassword());

        User loginUser = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "존재하지 않은 이메일입니다."));

        // 비밀번호 체크
        if (!encoder.matches(req.getPassword(), loginUser.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // JWT Token 발급
        String accessToken = JwtTokenUtil.createToken(loginUser.getEmail(), secretKey, accessTokenDurationSec * 1000);

        return UserLoginResponse.builder()
                .email(loginUser.getEmail())
                .jwtToken(accessToken)
                .build();
    }

    private static void validation(String email, String password) {
        if (email == null || email.equals("")) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "이메일은 비어있을 수 없습니다.");
        }
        if (!email.contains("@")) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "이메일 형식이 아닙니다.");
        }
        if (password == null || password.equals("")) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "비밀번호는 비어있을 수 없습니다.");
        }
        if (password.length() < 8) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "비밀번호는 8자 이상이여야 합니다.");
        }
    }
}
