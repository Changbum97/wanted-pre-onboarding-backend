package com.wanted.wantedpreonboardingbackend.domain.dto.board;

import com.wanted.wantedpreonboardingbackend.domain.entity.Board;
import com.wanted.wantedpreonboardingbackend.domain.entity.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequest {

    private String title;
    private String body;

    public Board toEntity(User user) {
        return Board.builder()
                .user(user)
                .title(this.title)
                .body(this.body)
                .build();
    }
}
