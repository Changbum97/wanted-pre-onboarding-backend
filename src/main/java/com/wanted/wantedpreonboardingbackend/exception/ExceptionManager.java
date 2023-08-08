package com.wanted.wantedpreonboardingbackend.exception;

import com.nts.ntsanchangbum.domain.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * RestControllerAdvice를 사용하여 RestController에서 CustomException이 발생하면
 * ExceptionManager에서 캐치하여 ResponseEntity로 변환 후 Return
 */
@RestControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity customExceptionHandler(CustomException e) {
        return Response.error(e);
    }
}
