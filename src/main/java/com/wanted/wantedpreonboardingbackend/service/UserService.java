package com.wanted.wantedpreonboardingbackend.service;

import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserCreateRequest;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserDto;
import com.wanted.wantedpreonboardingbackend.domain.entity.User;
import com.wanted.wantedpreonboardingbackend.exception.CustomException;
import com.wanted.wantedpreonboardingbackend.exception.ErrorCode;
import com.wanted.wantedpreonboardingbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserDto save(UserCreateRequest req) {
        if (req.getEmail() == null || req.getEmail().equals("")) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "이메일을 입력해주세요.");
        }
        if (!req.getEmail().contains("@")) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "이메일 형식이 아닙니다.");
        }
        if (req.getPassword() == null || req.getPassword().equals("")) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "비밀번호를 입력해주세요.");
        }
        if (req.getPassword().length() < 8) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "비밀번호는 8자 이상이여야 합니다.");
        }

        String encodedPassword = encoder.encode(req.getPassword());
        User savedUser = userRepository.save( req.toEntity(encodedPassword) );
        return UserDto.of(savedUser);
    }
}
