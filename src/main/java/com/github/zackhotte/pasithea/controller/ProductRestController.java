package com.github.zackhotte.pasithea.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.zackhotte.pasithea.model.Book;
import com.github.zackhotte.pasithea.repositories.AuthorRepository;
import com.github.zackhotte.pasithea.repositories.BookRepository;
import com.github.zackhotte.pasithea.repositories.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
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

    @GetMapping
    public JsonNode getBooks(@RequestParam(value = "q", required = false, defaultValue = "") String q,
                             HttpServletRequest request) {
        if (q == null || q.isEmpty()) {
            return getAllBooks(request);
        }
        return searchForBook(q);
    }

    @GetMapping(path = "/{productId}")
    public Book productInformation(@PathVariable Long productId) {
        Validator.validateProductId(productId);
        return bookRepository.findOne(productId);
    }

    private JsonNode getAllBooks(HttpServletRequest request) {
        String origin = ServletUriComponentsBuilder.fromContextPath(request).toUriString();

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode node = mapper.createArrayNode();
        bookRepository.findAll().forEach(book -> {
            ObjectNode object = mapper.createObjectNode();
            object.put("id", book.getId());
            object.put("name", book.getName());
            object.put("category", "BOOK");
            object.put("quantity", book.getQuantity());
            object.put("price", book.getPrice());
            object.put("rating", book.getRating());
            object.put("imageUrl", book.getImageUrl());
            createLink(object, origin + "/api/products/" + book.getId(), "self");
            node.add(object);
        });
        return node;
    }

    private ObjectNode searchForBook(String q) {
        ObjectMapper mapper = new ObjectMapper();
        List<Book> books = bookRepository.findAll().stream()
                .filter(book -> book.getName().toLowerCase().contains(q.toLowerCase()))
                .collect(Collectors.toList());

        ObjectNode responseObject = mapper.createObjectNode();
        responseObject.put("q", q);

        ObjectNode res = mapper.createObjectNode();
        res.put("numFound", books.size());
        res.set("results", mapper.valueToTree(books));

        responseObject.set("res", res);
        return responseObject;
    }

    private void createLink(ObjectNode node, String href, String rel) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode linkRel = mapper.createObjectNode();
        linkRel.put("href", href);
        linkRel.put("rel", rel);

        ArrayNode links = mapper.createArrayNode();
        links.add(linkRel);
        node.set("link", links);
    }

}
