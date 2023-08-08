package com.wanted.wantedpreonboardingbackend.controller.api;

import com.wanted.wantedpreonboardingbackend.domain.Response;
import com.wanted.wantedpreonboardingbackend.domain.dto.board.BoardCreateRequest;
import com.wanted.wantedpreonboardingbackend.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardRestController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity save(@RequestBody BoardCreateRequest req, Authentication auth) {
        return Response.success(boardService.save(req, auth.getName()));
    }
}
