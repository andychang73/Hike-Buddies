package com.example.springsecurityjwt.exceiptions;

import com.example.springsecurityjwt.util.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@CrossOrigin
public class FileUploadExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Status> handleMaxSizeException(MaxUploadSizeExceededException exc){
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Status(409, "File too large!"));
    }
}
