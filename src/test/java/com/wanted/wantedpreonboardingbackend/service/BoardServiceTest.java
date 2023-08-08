package com.wanted.wantedpreonboardingbackend.service;

import com.wanted.wantedpreonboardingbackend.domain.dto.board.BoardDto;
import com.wanted.wantedpreonboardingbackend.domain.dto.board.BoardRequest;
import com.wanted.wantedpreonboardingbackend.domain.entity.Board;
import com.wanted.wantedpreonboardingbackend.domain.entity.User;
import com.wanted.wantedpreonboardingbackend.exception.CustomException;
import com.wanted.wantedpreonboardingbackend.exception.ErrorCode;
import com.wanted.wantedpreonboardingbackend.repository.BoardRepository;
import com.wanted.wantedpreonboardingbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BoardServiceTest {

    BoardService boardService;

    private final BoardRepository boardRepository = mock(BoardRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    Board board1;
    User user1;

    @BeforeEach
    void setUp() {
        // BoardService에 의존 주입
        boardService = new BoardService(boardRepository, userRepository);

        // User, Board 객체 생성
        user1 = User.builder().id(1L).email("test@naver.com").build();
        board1 = Board.builder().id(1L).title("제목1").body("내용1").user(user1).build();
    }

    @Test
    @DisplayName("게시글 작성 성공 Test")
    void saveSuccess() {
        // given
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user1));
        when(boardRepository.save(any())).thenReturn(board1);
        BoardRequest req = BoardRequest.builder().title("제목1").body("내용1").build();

        // when
        BoardDto boardDto = assertDoesNotThrow(() -> boardService.save(req, "test@naver.com"));

        // then
        assertEquals(1L, boardDto.getId());
        assertEquals("제목1", boardDto.getTitle());
        assertEquals("내용1", boardDto.getBody());
    }

    @Test
    @DisplayName("게시글 작성 실패 Test 1 - 유저가 없는 경우")
    void saveFail1() {
        // given
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(boardRepository.save(any())).thenReturn(board1);
        BoardRequest req = BoardRequest.builder().title("제목1").body("내용1").build();

        // when
        CustomException e = assertThrows(CustomException.class, () -> boardService.save(req, "test@naver.com"));

        // then
        assertEquals(ErrorCode.BAD_REQUEST, e.getErrorCode());
        assertEquals("유저가 존재하지 않습니다.", e.getMessage());
    }

    @Test
    @DisplayName("게시글 작성 실패 Test 2 - 제목이 null인 경우")
    void saveFail2() {
        // given
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user1));
        when(boardRepository.save(any())).thenReturn(board1);
        BoardRequest req = BoardRequest.builder().title("").body("내용1").build();

        // when
        CustomException e = assertThrows(CustomException.class, () -> boardService.save(req, "test@naver.com"));

        // then
        assertEquals(ErrorCode.BAD_REQUEST, e.getErrorCode());
        assertEquals("제목은 비어있을 수 없습니다.", e.getMessage());
    }

    @Test
    @DisplayName("게시글 리스트 조회 성공 Test")
    void listSuccess() {
        // given
        List<Board> boards = new ArrayList<>();
        boards.add(Board.builder().id(2L).title("제목2").body("내용2").user(user1).build());
        boards.add(board1);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Board> boardPage = new PageImpl<>(boards, pageable, 1);

        when(boardRepository.findAll(pageable)).thenReturn(boardPage);

        // when
        Page<BoardDto> boardDtoPage = assertDoesNotThrow(() -> boardService.list(pageable));

        // then
        assertEquals(2, boardDtoPage.getTotalElements());
    }

    @Test
    @DisplayName("게시글 조회 성공 Test")
    void detailSuccess() {
        // given
        when(boardRepository.findById(any())).thenReturn(Optional.of(board1));

        // when
        BoardDto boardDto = assertDoesNotThrow(() -> boardService.detail(1L));

        // then
        assertEquals(1L, boardDto.getId());
        assertEquals("제목1", boardDto.getTitle());
        assertEquals("내용1", boardDto.getBody());
    }

    @Test
    @DisplayName("게시글 조회 실패 Test - 게시글이 존재하지 않는 경우")
    void detailFail() {
        // given
        when(boardRepository.findById(any())).thenReturn(Optional.empty());

        // when
        CustomException e = assertThrows(CustomException.class, () -> boardService.detail(1L));

        // then
        assertEquals(ErrorCode.BAD_REQUEST, e.getErrorCode());
        assertEquals("게시글이 존재하지 않습니다.", e.getMessage());
    }

    @Test
    @DisplayName("게시글 수정 성공 Test")
    void editSuccess() {
        // given
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user1));
        when(boardRepository.findById(any())).thenReturn(Optional.of(board1));

        BoardRequest req = BoardRequest.builder().title("제목 수정 1").body("내용 수정 1").build();
        Board editedBoard = Board.builder().id(1L).title("제목 수정 1").body("내용 수정 1").user(user1).build();
        when(boardRepository.save(any())).thenReturn(editedBoard);

        // when
        BoardDto boardDto = assertDoesNotThrow(() -> boardService.edit(1L, req, user1.getEmail()));

        // then
        assertEquals(1L, boardDto.getId());
        assertEquals("제목 수정 1", boardDto.getTitle());
    }

    @Test
    @DisplayName("게시글 수정 실패 Test 1 - 게시글이 존재하지 않는 경우")
    void editFail1() {
        // given
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user1));
        when(boardRepository.findById(any())).thenReturn(Optional.empty());

        BoardRequest req = BoardRequest.builder().title("제목 수정 1").body("내용 수정 1").build();
        Board editedBoard = Board.builder().id(1L).title("제목 수정 1").body("내용 수정 1").user(user1).build();
        when(boardRepository.save(any())).thenReturn(editedBoard);

        // when
        CustomException e = assertThrows(CustomException.class, () -> boardService.edit(1L, req, user1.getEmail()));

        // then
        assertEquals(ErrorCode.BAD_REQUEST, e.getErrorCode());
        assertEquals("게시글이 존재하지 않습니다.", e.getMessage());
    }

    @Test
    @DisplayName("게시글 수정 실패 Test 2 - 유저가 존재하지 않는 경우")
    void editFail2() {
        // given
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(boardRepository.findById(any())).thenReturn(Optional.of(board1));

        BoardRequest req = BoardRequest.builder().title("제목 수정 1").body("내용 수정 1").build();
        Board editedBoard = Board.builder().id(1L).title("제목 수정 1").body("내용 수정 1").user(user1).build();
        when(boardRepository.save(any())).thenReturn(editedBoard);

        // when
        CustomException e = assertThrows(CustomException.class, () -> boardService.edit(1L, req, user1.getEmail()));

        // then
        assertEquals(ErrorCode.BAD_REQUEST, e.getErrorCode());
        assertEquals("유저가 존재하지 않습니다.", e.getMessage());
    }

    @Test
    @DisplayName("게시글 수정 실패 Test 3 - 본인이 작성한 게시글이 아닌 경우")
    void editFail3() {
        // given
        User user2 = User.builder().id(1L).email("test2@naver.com").build();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user2));
        when(boardRepository.findById(any())).thenReturn(Optional.of(board1));

        BoardRequest req = BoardRequest.builder().title("제목 수정 1").body("내용 수정 1").build();
        Board editedBoard = Board.builder().id(1L).title("제목 수정 1").body("내용 수정 1").user(user1).build();
        when(boardRepository.save(any())).thenReturn(editedBoard);

        // when
        CustomException e = assertThrows(CustomException.class, () -> boardService.edit(1L, req, user2.getEmail()));

        // then
        assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
        assertEquals( "본인이 작성한 게시글만 수정 가능합니다.", e.getMessage());
    }

    @Test
    @DisplayName("게시글 삭제 성공 Test")
    void deleteSuccess() {
        // given
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user1));
        when(boardRepository.findById(any())).thenReturn(Optional.of(board1));

        // when
        String result = assertDoesNotThrow(() -> boardService.delete(1L,  user1.getEmail()));

        // then
        assertEquals("1번 글이 삭제되었습니다.", result);
    }

    @Test
    @DisplayName("게시글 삭제 실패 Test 1 - 게시글이 존재하지 않는 경우")
    void deleteFail1() {
        // given
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user1));
        when(boardRepository.findById(any())).thenReturn(Optional.empty());

        // when
        CustomException e = assertThrows(CustomException.class, () -> boardService.delete(1L, user1.getEmail()));

        // then
        assertEquals(ErrorCode.BAD_REQUEST, e.getErrorCode());
        assertEquals("게시글이 존재하지 않습니다.", e.getMessage());
    }

    @Test
    @DisplayName("게시글 삭제 실패 Test 2 - 유저가 존재하지 않는 경우")
    void deleteFail2() {
        // given
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(boardRepository.findById(any())).thenReturn(Optional.of(board1));

        // when
        CustomException e = assertThrows(CustomException.class, () -> boardService.delete(1L, user1.getEmail()));

        // then
        assertEquals(ErrorCode.BAD_REQUEST, e.getErrorCode());
        assertEquals("유저가 존재하지 않습니다.", e.getMessage());
    }

    @Test
    @DisplayName("게시글 삭제 실패 Test 3 - 본인이 작성한 게시글이 아닌 경우")
    void deleteFail3() {
        // given
        User user2 = User.builder().id(1L).email("test2@naver.com").build();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user2));
        when(boardRepository.findById(any())).thenReturn(Optional.of(board1));

        // when
        CustomException e = assertThrows(CustomException.class, () -> boardService.delete(1L, user2.getEmail()));

        // then
        assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
        assertEquals( "본인이 작성한 게시글만 삭제 가능합니다.", e.getMessage());
    }
}