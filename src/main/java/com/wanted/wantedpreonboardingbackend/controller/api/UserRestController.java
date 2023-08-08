package com.wanted.wantedpreonboardingbackend.controller.api;

import com.wanted.wantedpreonboardingbackend.domain.Response;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserCreateRequest;
import com.wanted.wantedpreonboardingbackend.domain.dto.user.UserLoginRequest;
import com.wanted.wantedpreonboardingbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity save(@RequestBody UserCreateRequest req) {
        return Response.success(userService.save(req));
    }

    @GetMapping("/login")
    public ResponseEntity login(@RequestBody UserLoginRequest req) {
        return Response.success(userService.login(req));
    }
}
