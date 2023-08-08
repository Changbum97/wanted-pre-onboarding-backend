package com.wanted.wantedpreonboardingbackend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.wantedpreonboardingbackend.domain.dto.board.BoardDto;
import com.wanted.wantedpreonboardingbackend.domain.dto.board.BoardRequest;
import com.wanted.wantedpreonboardingbackend.exception.CustomException;
import com.wanted.wantedpreonboardingbackend.exception.ErrorCode;
import com.wanted.wantedpreonboardingbackend.service.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardRestController.class)
class BoardRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BoardService boardService;

    @Test
    @DisplayName("게시글 작성 성공 Test")
    @WithMockUser
    void saveSuccess() throws Exception {
        // when
        BoardDto boardDto = BoardDto.builder().id(1L).title("제목1").body("내용1").userEmail("test@naver.com").build();
        when(boardService.save(any(), any())).thenReturn(boardDto);
        BoardRequest req = BoardRequest.builder().title("제목1").body("내용1").build();

        // then, given
        mockMvc.perform(post("/api/boards")
                    .content(objectMapper.writeValueAsString(req))
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.title").value(req.getTitle()))
                .andExpect(jsonPath("$.result.body").value(req.getBody()))
                .andDo(print());

        verify(boardService).save(any(), any());
    }

    @Test
    @DisplayName("게시글 작성 실패 Test - 로그인을 하지 않은 경우")
    @WithAnonymousUser
    void saveFail() throws Exception {
        // when
        BoardDto boardDto = BoardDto.builder().id(1L).title("제목1").body("내용1").userEmail("test@naver.com").build();
        when(boardService.save(any(), any())).thenReturn(boardDto);
        BoardRequest req = BoardRequest.builder().title("제목1").body("내용1").build();

        // then, given
        mockMvc.perform(post("/api/boards")
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 리스트 조회 성공 Test")
    @WithMockUser
    void listSuccess() throws Exception {
        // when
        List<BoardDto> boardDtoList = new ArrayList<>();
        BoardDto boardDto1 = BoardDto.builder().id(1L).title("제목1").body("내용1").userEmail("test@naver.com").build();
        BoardDto boardDto2 = BoardDto.builder().id(2L).title("제목2").body("내용2").userEmail("test@naver.com").build();
        boardDtoList.add(boardDto1);
        boardDtoList.add(boardDto2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<BoardDto> boardDtoPage = new PageImpl<>(boardDtoList, pageable, 1);
        when(boardService.list(any())).thenReturn(boardDtoPage);

        // then, given
        mockMvc.perform(get("/api/boards?page=0")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.content.size()").value(2))
                .andDo(print());

        verify(boardService).list(any());
    }

    @Test
    @DisplayName("게시글 조회 성공 Test")
    @WithMockUser
    void detailSuccess() throws Exception {
        // when
        BoardDto boardDto = BoardDto.builder().id(1L).title("제목1").body("내용1").userEmail("test@naver.com").build();
        when(boardService.detail(any())).thenReturn(boardDto);

        // then, given
        mockMvc.perform(get("/api/boards/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.title").value("제목1"))
                .andDo(print());

        verify(boardService).detail(any());
    }

    @Test
    @DisplayName("게시글 조회 실패 Test - 게시글이 없는 경우")
    @WithMockUser
    void detailFail() throws Exception {
        // when
        when(boardService.detail(any())).thenThrow(new CustomException(ErrorCode.BAD_REQUEST, "게시글이 존재하지 않습니다."));

        // then, given
        mockMvc.perform(get("/api/boards/1")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value( "게시글이 존재하지 않습니다."))
                .andDo(print());

        verify(boardService).detail(any());
    }

    @Test
    @DisplayName("게시글 수정 성공 Test")
    @WithMockUser
    void editSuccess() throws Exception {
        // when
        BoardDto boardDto = BoardDto.builder().id(1L).title("제목 수정 1").body("내용 수정 1").userEmail("test@naver.com").build();
        when(boardService.edit(any(), any(), any())).thenReturn(boardDto);
        BoardRequest req = BoardRequest.builder().title("제목 수정 1").body("내용 수정 1").build();

        // then, given
        mockMvc.perform(put("/api/boards/1")
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.title").value(req.getTitle()))
                .andExpect(jsonPath("$.result.body").value(req.getBody()))
                .andDo(print());

        verify(boardService).edit(any(), any(), any());
    }

    @Test
    @DisplayName("게시글 수정 실패 Test - 본인이 작성한 게시글이 아닌 경우")
    @WithMockUser
    void editFail() throws Exception {
        // when
        when(boardService.edit(any(), any(), any())).thenThrow(new CustomException(ErrorCode.INVALID_PERMISSION, "본인이 작성한 게시글만 수정 가능합니다."));
        BoardRequest req = BoardRequest.builder().title("제목1").body("내용1").build();

        // then, given
        mockMvc.perform(put("/api/boards/1")
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value("본인이 작성한 게시글만 수정 가능합니다."))
                .andDo(print());

        verify(boardService).edit(any(), any(), any());

    }

    @Test
    @DisplayName("게시글 삭제 성공 Test")
    @WithMockUser
    void deleteSuccess() throws Exception {
        // when
        when(boardService.delete(any(), any())).thenReturn("1번 글이 삭제되었습니다.");

        // then, given
        mockMvc.perform(delete("/api/boards/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").value("1번 글이 삭제되었습니다."))
                .andDo(print());

        verify(boardService).delete(any(), any());
    }

    @Test
    @DisplayName("게시글 삭제 실패 Test - 본인이 작성한 게시글이 아닌 경우")
    @WithMockUser
    void deleteFail() throws Exception {
        // when
        when(boardService.delete(any(), any())).thenThrow(new CustomException(ErrorCode.INVALID_PERMISSION, "본인이 작성한 게시글만 삭제 가능합니다."));

        // then, given
        mockMvc.perform(delete("/api/boards/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value("본인이 작성한 게시글만 삭제 가능합니다."))
                .andDo(print());

        verify(boardService).delete(any(), any());
    }
}