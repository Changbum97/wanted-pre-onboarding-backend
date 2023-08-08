package com.wanted.wantedpreonboardingbackend.domain.dto.board;

import com.wanted.wantedpreonboardingbackend.domain.entity.Board;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {

    private Long id;
    private String userEmail;
    private String title;
    private String body;

    public static BoardDto of(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .userEmail(board.getUser().getEmail())
                .title(board.getTitle())
                .body(board.getBody())
                .build();
    }
}
