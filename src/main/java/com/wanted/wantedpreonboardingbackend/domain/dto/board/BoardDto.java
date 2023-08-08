package com.wanted.wantedpreonboardingbackend.domain.dto.board;

import com.wanted.wantedpreonboardingbackend.domain.entity.Board;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public static BoardDto of(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .userEmail(board.getUser().getEmail())
                .title(board.getTitle())
                .body(board.getBody())
                .createdAt(board.getCreatedAt())
                .lastModifiedAt(board.getLastModifiedAt())
                .build();
    }
}
