package com.wanted.wantedpreonboardingbackend.service;

import com.wanted.wantedpreonboardingbackend.domain.dto.board.BoardCreateRequest;
import com.wanted.wantedpreonboardingbackend.domain.dto.board.BoardDto;
import com.wanted.wantedpreonboardingbackend.domain.entity.Board;
import com.wanted.wantedpreonboardingbackend.domain.entity.User;
import com.wanted.wantedpreonboardingbackend.exception.CustomException;
import com.wanted.wantedpreonboardingbackend.exception.ErrorCode;
import com.wanted.wantedpreonboardingbackend.repository.BoardRepository;
import com.wanted.wantedpreonboardingbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public BoardDto save(BoardCreateRequest req, String userEmail) {
        User loginUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "유저가 존재하지 않습니다."));

        validation(req.getTitle(), req.getBody());

        Board newBoard = Board.builder()
                .user(loginUser)
                .title(req.getTitle())
                .body(req.getBody())
                .build();

        Board savedBoard = boardRepository.save(newBoard);
        return BoardDto.of(savedBoard);
    }

    public Page<BoardDto> list(Pageable pageable) {
        Page<Board> boardPage = boardRepository.findAll(pageable);

        // Page<Board> -> Page<BoardDto>로 변환 후 return
        return boardPage.map(board -> BoardDto.of(board));
    }

    public BoardDto detail(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "게시글이 존재하지 않습니다."));
        return BoardDto.of(board);
    }

    private static void validation(String title, String body) {
        if (title == null || title.equals("")) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "제목을 입력해주세요.");
        }
        if (body == null || body.equals("")) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "내용을 입력해주세요.");
        }
    }
}
