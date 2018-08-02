package com.github.zackhotte.pasithea.controller;

import com.github.zackhotte.pasithea.model.OutOfStockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ProductRestControllerAdvice {

    @ExceptionHandler(value = OutOfStockException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ResponseEntity<Response> outOfStockExceptionHandler(OutOfStockException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new Response("Out of Stock", e.getMessage(), 403)
        );
    }

}
