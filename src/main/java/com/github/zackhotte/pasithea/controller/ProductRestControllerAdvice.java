package com.github.zackhotte.pasithea.controller;

import com.github.zackhotte.pasithea.model.OutOfStockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ProductRestControllerAdvice {

    @ExceptionHandler(value = OutOfStockException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ResponseEntity<Response> outOfStockExceptionHandler(OutOfStockException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Response.error("Out of Stock", e.getMessage(), 403)
        );
    }

    @ExceptionHandler(value = {IOException.class, URISyntaxException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Response> ioExceptionHandler(IOException e) {
        return ResponseEntity.badRequest().body(
                Response.error("Error parsing the request body", e.getMessage(), 400)
        );
    }

    @ExceptionHandler(value = NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<Response> noSuchElementExceptionHandler(NoSuchElementException e) {
        return new ResponseEntity<>(
                Response.error("Item id not found", e.getMessage(), 404),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(value = InvalidJsonDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Response> invalidDataHandler(InvalidJsonDataException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Response.error("Error processing the request body", e.getMessage(), 400)
        );
    }

}
