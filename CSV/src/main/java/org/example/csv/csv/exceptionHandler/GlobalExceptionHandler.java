package org.example.csv.csv.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<String> invalidFileException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid File Passed");
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<String> internalSeverException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
