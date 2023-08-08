package com.wanted.wantedpreonboardingbackend.service;

import com.wanted.wantedpreonboardingbackend.domain.BaseEntity;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserCreateRequest;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserDto;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserLoginRequest;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserLoginResponse;
import com.wanted.wantedpreonboardingbackend.domain.entity.User;
import com.wanted.wantedpreonboardingbackend.exception.CustomException;
import com.wanted.wantedpreonboardingbackend.exception.ErrorCode;
import com.wanted.wantedpreonboardingbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    UserService userService;
    private final UserRepository userRepository = mock(UserRepository.class);

    User user1;

    @BeforeEach
    void setUp() {
        // UserService에 의존 주입
        userService = new UserService(userRepository, new BCryptPasswordEncoder());
        ReflectionTestUtils.setField(userService, "secretKey", "secretKeyForTest");
        ReflectionTestUtils.setField(userService, "accessTokenDurationSec", 1800L);

        // User 객체 생성
        String encodedPassword = new BCryptPasswordEncoder().encode("12345678");
        user1 = User.builder().id(1L).email("test@naver.com").password(encodedPassword).build();

        // createdAt은 BaseEntity로 인해 자동으로 설정되기 때문에 테스트 코드에선 따로 설정 필요
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(user1, BaseEntity.class, "createdAt", now, LocalDateTime.class);
    }

    @Test
    @DisplayName("회원가입 성공 Test")
    void joinSuccess() {
        // given
        when(userRepository.save(any())).thenReturn(user1);
        UserCreateRequest req = UserCreateRequest.builder().email("test@naver.com").password("12345678").build();

        // when
        UserDto userDto = assertDoesNotThrow(() -> userService.save(req));

        // then
        assertEquals(1L, userDto.getId());
        assertEquals(req.getEmail(), userDto.getEmail());
    }

    @Test
    @DisplayName("회원가입 실패 Test 1 - Email에 @가 들어가지 않은 경우")
    void joinFail1() {
        // given
        UserCreateRequest req = UserCreateRequest.builder().email("test").password("12345678").build();

        // when
        CustomException e = assertThrows(CustomException.class, () -> userService.save(req));

        // then
        assertEquals(ErrorCode.BAD_REQUEST, e.getErrorCode());
        assertEquals("이메일 형식이 아닙니다.", e.getMessage());
    }

    @Test
    @DisplayName("회원가입 실패 Test 2 - 비밀번호가 8자 미만인 경우")
    void joinFail2() {
        // given
        UserCreateRequest req = UserCreateRequest.builder().email("test@naver.com").password("1234567").build();

        // when
        CustomException e = assertThrows(CustomException.class, () -> userService.save(req));

        // then
        assertEquals(ErrorCode.BAD_REQUEST, e.getErrorCode());
        assertEquals("비밀번호는 8자 이상이여야 합니다.", e.getMessage());
    }

    @Test
    @DisplayName("로그인 성공 Test")
    void loginSuccess() {
        // given
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user1));
        UserLoginRequest req = UserLoginRequest.builder().email("test@naver.com").password("12345678").build();

        // when
        UserLoginResponse res = assertDoesNotThrow(() -> userService.login(req));

        // then
        assertEquals("test@naver.com", res.getEmail());
        assertNotNull(res.getJwtToken());
    }

    @Test
    @DisplayName("로그인 실패 Test 1 - email이 존재하지 않는 경우")
    void loginFail1() {
        // given
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        UserLoginRequest req = UserLoginRequest.builder().email("test@naver.com").password("11111111").build();

        // when
        CustomException e = assertThrows(CustomException.class, () -> userService.login(req));

        // then
        assertEquals(ErrorCode.BAD_REQUEST, e.getErrorCode());
        assertEquals("존재하지 않은 이메일입니다.", e.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 Test 2 - 비밀번호가 틀린 경우")
    void loginFail2() {
        // given
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user1));
        UserLoginRequest req = UserLoginRequest.builder().email("test@naver.com").password("11111111").build();

        // when
        CustomException e = assertThrows(CustomException.class, () -> userService.login(req));

        // then
        assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());
    }
}