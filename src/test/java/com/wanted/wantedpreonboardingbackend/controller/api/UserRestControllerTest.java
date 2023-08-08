package com.wanted.wantedpreonboardingbackend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserCreateRequest;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserDto;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserLoginResponse;
import com.wanted.wantedpreonboardingbackend.exception.CustomException;
import com.wanted.wantedpreonboardingbackend.exception.ErrorCode;
import com.wanted.wantedpreonboardingbackend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
class UserRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Test
    @DisplayName("회원가입 성공 Test")
    @WithMockUser
    void joinSuccess() throws Exception {
        // when
        UserDto userDto = UserDto.builder().id(1L).email("test@naver.com").build();
        when(userService.save(any())).thenReturn(userDto);
        UserCreateRequest req = UserCreateRequest.builder().email("test@naver.com").password("12345678").build();

        // then, given
        mockMvc.perform(post("/api/users/join")
                    .content(objectMapper.writeValueAsString(req))
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.email").value(req.getEmail()))
                .andDo(print());

        verify(userService).save(any());
    }

    @Test
    @DisplayName("회원가입 실패 Test 1 - Email에 @가 들어가지 않은 경우")
    @WithMockUser
    void joinFail1() throws Exception {
        // when
        when(userService.save(any())).thenThrow(new CustomException(ErrorCode.BAD_REQUEST, "이메일 형식이 아닙니다."));
        UserCreateRequest req = UserCreateRequest.builder().email("test").password("12345678").build();

        // then, given
        mockMvc.perform(post("/api/users/join")
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value("이메일 형식이 아닙니다."))
                .andDo(print());

        verify(userService).save(any());
    }

    @Test
    @DisplayName("회원가입 실패 Test 2 - 비밀번호가 8자 미만인 경우")
    @WithMockUser
    void joinFail2() throws Exception {
        // when
        when(userService.save(any())).thenThrow(new CustomException(ErrorCode.BAD_REQUEST, "비밀번호는 8자 이상이여야 합니다."));
        UserCreateRequest req = UserCreateRequest.builder().email("test@naver.com").password("12345678").build();

        // then, given
        mockMvc.perform(post("/api/users/join")
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value("비밀번호는 8자 이상이여야 합니다."))
                .andDo(print());

        verify(userService).save(any());
    }

    @Test
    @DisplayName("로그인 성공 Test")
    @WithMockUser
    void loginSuccess() throws Exception {
        // when
        UserLoginResponse res = UserLoginResponse.builder().email("test@naver.com").jwtToken("token1234").build();
        when(userService.login(any())).thenReturn(res);
        UserCreateRequest req = UserCreateRequest.builder().email("test@naver.com").password("12345678").build();

        // then, given
        mockMvc.perform(post("/api/users/login")
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.email").value("test@naver.com"))
                .andExpect(jsonPath("$.result.jwtToken").value("token1234"))
                .andDo(print());

        verify(userService).login(any());
    }

    @Test
    @DisplayName("로그인 실패 Test - 비밀번호가 틀린 경우")
    @WithMockUser
    void loginFail() throws Exception {
        // when
        when(userService.login(any())).thenThrow(new CustomException(ErrorCode.INVALID_PASSWORD));
        UserCreateRequest req = UserCreateRequest.builder().email("test@naver.com").password("11111111").build();

        // then, given
        mockMvc.perform(post("/api/users/login")
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value("비밀번호가 일치하지 않습니다."))
                .andDo(print());

        verify(userService).login(any());
    }
}