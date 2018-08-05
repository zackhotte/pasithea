package com.github.zackhotte.pasithea.controller;

import com.github.zackhotte.pasithea.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class Validator {

    private static BookRepository bookRepository;

    @Autowired
    public Validator(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public static void validateProductId(Long productId) {
        if (bookRepository.findOne(productId) == null) {
            throw new NoSuchElementException("Could not find product id " + productId);
        }
    }

}
