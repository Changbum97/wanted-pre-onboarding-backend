package com.wanted.wantedpreonboardingbackend.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private ErrorCode errorCode;
    private String message;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    /**
     *    메소드 오버로딩을 사용하여 message가 따로 입력되지 않으면 ErrorCode의 기본 메세지를 담아주고
     *    message가 따로 입력되면 입력된 메세지를 담아줌
     */
    public CustomException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
