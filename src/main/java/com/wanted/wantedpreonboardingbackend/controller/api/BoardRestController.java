package com.wanted.wantedpreonboardingbackend.controller.api;

import com.wanted.wantedpreonboardingbackend.domain.Response;
import com.wanted.wantedpreonboardingbackend.domain.dto.board.BoardRequest;
import com.wanted.wantedpreonboardingbackend.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardRestController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity save(@RequestBody BoardRequest req, Authentication auth) {
        return Response.success(boardService.save(req, auth.getName()));
    }

    @GetMapping
    public ResponseEntity list(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable) {
        return Response.success(boardService.list(pageable));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity detail(@PathVariable Long boardId) {
        return Response.success(boardService.detail(boardId));
    }

    @PutMapping("/{boardId}")
    public ResponseEntity edit(@PathVariable Long boardId, @RequestBody BoardRequest req, Authentication auth) {
        return Response.success(boardService.edit(boardId, req, auth.getName()));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity delete(@PathVariable Long boardId, Authentication auth) {
        return Response.success(boardService.delete(boardId, auth.getName()));
    }
}
