package com.github.zackhotte.pasithea.controller;

import com.github.zackhotte.pasithea.model.AuthorRepository;
import com.github.zackhotte.pasithea.model.Book;
import com.github.zackhotte.pasithea.model.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductRestController {

    private BookRepository bookRepository;
    private AuthorRepository authorRepository;

    @Autowired
    public ProductRestController(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @GetMapping(path = "/{productId}")
    public Book productInformation(@PathVariable Long productId) {
        return bookRepository.findOne(productId);
    }

}
