package com.wanted.wantedpreonboardingbackend.domain;

import com.wanted.wantedpreonboardingbackend.exception.CustomException;
import com.wanted.wantedpreonboardingbackend.exception.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/**
 * API 통신 시 사용할 Response
 */
@Getter
@AllArgsConstructor
public class Response<T> {

    private String resultCode;
    private T result;

    public static ResponseEntity error(CustomException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(new Response<>("ERROR", ErrorResponse.of(e)));
    }

    public static <T> ResponseEntity success(T resultObject) {
        return ResponseEntity.ok()
                .body(new Response<>("SUCCESS", resultObject));
    }
}
