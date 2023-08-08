package com.wanted.wantedpreonboardingbackend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * CustomException을 그대로 출력한다면 Exception에 대한 정보들이 모두 출력됨
 * 이를 방지하고 CustomException의 name만 출력하기 위해 사용하는 DTO
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String errorCode;
    private String message;

    public static ErrorResponse of(CustomException e) {
        return new ErrorResponse(e.getErrorCode().name(), e.getMessage());
    }
}
