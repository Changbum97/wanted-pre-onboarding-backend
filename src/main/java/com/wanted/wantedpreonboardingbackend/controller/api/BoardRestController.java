package com.wanted.wantedpreonboardingbackend.controller.api;

import com.wanted.wantedpreonboardingbackend.domain.Response;
import com.wanted.wantedpreonboardingbackend.domain.dto.board.BoardRequest;
import com.wanted.wantedpreonboardingbackend.service.BoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Api(description = "게시글 관련 API")
public class BoardRestController {

    private final BoardService boardService;

    @PostMapping
    @ApiOperation(value = "게시글 등록 API",
            notes = "로그인한 유저만 게시글을 등록할 수 있습니다.<br/>title, body는 Null일 수 없습니다.")
    public ResponseEntity save(@RequestBody BoardRequest req, @ApiIgnore Authentication auth) {
        return Response.success(boardService.save(req, auth.getName()));
    }

    @GetMapping
    @ApiOperation(value = "게시글 리스트 조회 API",
            notes = "게시글 리스트를 조회할 수 있습니다.<br/>page는 1부터 시작하고, 최신순으로 10개씩 출력됩니다.")
    @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", value = "1 ~ N 입력", defaultValue = "1")
    public ResponseEntity list(@ApiIgnore @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable) {
        return Response.success(boardService.list(pageable));
    }

    @GetMapping("/{boardId}")
    @ApiOperation(value = "게시글 상세 조회 API", notes = "게시글 하나를 조회할 수 있습니다.")
    @ApiImplicitParam(name = "boardId", value = "게시글 Id", defaultValue = "1")
    public ResponseEntity detail(@PathVariable Long boardId) {
        return Response.success(boardService.detail(boardId));
    }

    @PutMapping("/{boardId}")
    @ApiOperation(value = "게시글 수정 API", notes = "게시글의 title, body를 수정할 수 있습니다.<br/>title, body는 Null일 수 없습니다.<br/>본인이 작성한 게시글만 수정 가능합니다.")
    @ApiImplicitParam(name = "boardId", value = "게시글 Id", defaultValue = "1")
    public ResponseEntity edit(@PathVariable Long boardId, @RequestBody BoardRequest req,
                               @ApiIgnore Authentication auth) {
        return Response.success(boardService.edit(boardId, req, auth.getName()));
    }

    @DeleteMapping("/{boardId}")
    @ApiOperation(value = "게시글 삭제 API", notes = "게시글을 삭제할 수 있습니다..<br/>본인이 작성한 게시글만 삭제 가능합니다.")
    @ApiImplicitParam(name = "boardId", value = "게시글 Id", defaultValue = "1")
    public ResponseEntity delete(@PathVariable Long boardId, @ApiIgnore Authentication auth) {
        return Response.success(boardService.delete(boardId, auth.getName()));
    }
}
