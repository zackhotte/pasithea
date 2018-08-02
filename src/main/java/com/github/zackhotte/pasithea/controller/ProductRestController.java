package com.github.zackhotte.pasithea.controller;

import com.github.zackhotte.pasithea.model.AuthorRepository;
import com.github.zackhotte.pasithea.model.Book;
import com.github.zackhotte.pasithea.model.BookRepository;
import com.github.zackhotte.pasithea.model.ShoppingCart;
import com.github.zackhotte.pasithea.model.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductRestController {

    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    public ProductRestController(BookRepository bookRepository, AuthorRepository authorRepository,
                                 ShoppingCartRepository shoppingCartRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.shoppingCartRepository = shoppingCartRepository;
    }

    @GetMapping(path = "/{productId}")
    public Book productInformation(@PathVariable Long productId) {
        return bookRepository.findOne(productId);
    }

    @GetMapping(path = "/shoppingcart")
    public List<ShoppingCart> shoppingCart() {
        return shoppingCartRepository.findAll();
    }

    @GetMapping(path = "/shoppingcart/{shoppingCartId}")
    public ShoppingCart getItemInShoppingCart(@PathVariable Long shoppingCartId) {
        return shoppingCartRepository.findOne(shoppingCartId);
    }

}
