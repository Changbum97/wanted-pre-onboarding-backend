package com.wanted.wantedpreonboardingbackend.controller.api;

import com.wanted.wantedpreonboardingbackend.domain.Response;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserCreateRequest;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserLoginRequest;
import com.wanted.wantedpreonboardingbackend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Api(description = "유저 관련 API")
public class UserRestController {

    private final UserService userService;

    @PostMapping("/join")
    @ApiOperation(value = "회원가입 API", notes = "email, password로 회원가입 할 수 있습니다.<br/>email은 Null일 수 없고, @가 포함되야 합니다.<br/>password는 Null일 수 없고, 8자 이상이여야 합니다.")
    public ResponseEntity save(@RequestBody UserCreateRequest req) {
        return Response.success(userService.save(req));
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인 API", notes = "email, password로 로그인 할 수 있습니다.<br/>로그인 성공 시 Token이 발급됩니다.<br/>Swagger 문서의 우측 상단에 Authorize의 value에 'Bearer '+ Token을 넣으면 로그인 가능합니다.")
    public ResponseEntity login(@RequestBody UserLoginRequest req) {
        return Response.success(userService.login(req));
    }
}
