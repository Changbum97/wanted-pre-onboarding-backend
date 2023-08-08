package com.wanted.wantedpreonboardingbackend.service;

import com.wanted.wantedpreonboardingbackend.domain.dto.board.BoardRequest;
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

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public BoardDto save(BoardRequest req, String userEmail) {
        User loginUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "유저가 존재하지 않습니다."));

        validation(req.getTitle(), req.getBody());

        Board savedBoard = boardRepository.save(req.toEntity(loginUser));
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

    public BoardDto edit(Long boardId, BoardRequest req, String userEmail) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "게시글이 존재하지 않습니다."));

        validation(req.getTitle(), req.getBody());

        User loginUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "유저가 존재하지 않습니다."));

        if (!board.getUser().equals(loginUser)) {
            throw new CustomException(ErrorCode.INVALID_PERMISSION, "본인이 작성한 게시글만 수정 가능합니다.");
        }

        board.update(req.getTitle(), req.getBody());
        Board editedBoard = boardRepository.save(board);

        return BoardDto.of(editedBoard);
    }

    public String delete(Long boardId, String userEmail) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "게시글이 존재하지 않습니다."));

        User loginUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "유저가 존재하지 않습니다."));

        if (!board.getUser().equals(loginUser)) {
            throw new CustomException(ErrorCode.INVALID_PERMISSION, "본인이 작성한 게시글만 삭제 가능합니다.");
        }

        boardRepository.delete(board);
        return board.getId() + "번 글이 삭제되었습니다.";
    }

    private static void validation(String title, String body) {
        if (title == null || title.equals("")) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "제목은 비어있을 수 없습니다.");
        }
        if (body == null || body.equals("")) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "내용은 비어있을 수 없습니다.");
        }
    }
}
