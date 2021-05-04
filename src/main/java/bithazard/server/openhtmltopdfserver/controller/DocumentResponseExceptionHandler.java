package bithazard.server.openhtmltopdfserver.controller;

import bithazard.server.openhtmltopdfserver.template.UnknownExtensionException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;

@ControllerAdvice
class DocumentResponseExceptionHandler {
    @ExceptionHandler(JsonProcessingException.class)
    ResponseEntity<ErrorMessage> handleJsonProcessingException(JsonProcessingException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UnknownExtensionException.class)
    ResponseEntity<ErrorMessage> handleUnknownExtensionException(UnknownExtensionException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    ResponseEntity<ErrorMessage> handleTemplateNotFoundException(FileNotFoundException ex) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<ErrorMessage> buildResponseEntity(HttpStatus httpStatus, String errorMessage) {
        return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON).body(new ErrorMessage(errorMessage));
    }
}
