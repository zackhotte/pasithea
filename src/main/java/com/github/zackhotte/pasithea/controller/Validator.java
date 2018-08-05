package com.github.zackhotte.pasithea.controller;

import com.github.zackhotte.pasithea.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
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

    public static void validateAddingItemToCart(Map<String, String> body) throws InvalidJsonDataException {
        if (!body.containsKey("id")) {
            throw new InvalidJsonDataException("JSON payload is missing the product id");
        }

        if (!body.containsKey("quantity")) {
            throw new InvalidJsonDataException("JSON payload is missing the quantity amount to add to the cart");
        }

        if (!isNumeric(body.get("id"))) {
            throw new InvalidJsonDataException("Could not parse the 'id' data");
        }

        if (!isNumeric(body.get("quantity"))) {
            throw new InvalidJsonDataException("Could not parse the 'quantity' data");
        }
    }

    public static void validateRemovingItemFromCart(Map<String, String> body) throws InvalidJsonDataException {
        if (!body.containsKey("id")) {
            throw new InvalidJsonDataException("JSON payload is missing the product id");
        }

        if (!isNumeric(body.get("id"))) {
            throw new InvalidJsonDataException("Could not parse the 'id' data");
        }
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

}
