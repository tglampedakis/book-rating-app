package com.example.morobooks.exception;

import com.example.morobooks.dto.GenericApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<GenericApiResponseDto> handleNotFoundException(BookNotFoundException ex) {
        GenericApiResponseDto errorResponse = GenericApiResponseDto.builder()
                .success(Boolean.FALSE)
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<GenericApiResponseDto> handleValidationException(ValidationException ex) {
        GenericApiResponseDto errorResponse = GenericApiResponseDto.builder()
                .success(Boolean.FALSE)
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericApiResponseDto> handleGenericException(Exception ex) {
        GenericApiResponseDto errorResponse = GenericApiResponseDto.builder()
                .success(Boolean.FALSE)
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
