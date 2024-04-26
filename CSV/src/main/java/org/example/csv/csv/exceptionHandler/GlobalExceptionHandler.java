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

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<String> invalidIdException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Id Passed");
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<String> invalidArgumentException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Argument Passed");
    }
}
