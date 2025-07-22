package com.company.exceptions.handler;

import com.company.exceptions.UserNotFound;
import com.company.models.dto.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFound(UserNotFound e){
        return ResponseEntity.status(NOT_FOUND)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(e.getErrorCode())
                                .errorMessage(e.getErrorMessage())
                                .build()
                );
    }


}
