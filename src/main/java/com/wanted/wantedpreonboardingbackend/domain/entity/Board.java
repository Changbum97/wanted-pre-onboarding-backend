package com.wanted.wantedpreonboardingbackend.domain.entity;

import com.wanted.wantedpreonboardingbackend.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Board extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public void update(String newTitle, String newBody) {
        this.title = newTitle;
        this.body = newBody;
    }
}
