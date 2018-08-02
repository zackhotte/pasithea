package com.github.zackhotte.pasithea.controller;

import com.github.zackhotte.pasithea.model.AuthorRepository;
import com.github.zackhotte.pasithea.model.Book;
import com.github.zackhotte.pasithea.model.BookRepository;
import com.github.zackhotte.pasithea.model.OutOfStockException;
import com.github.zackhotte.pasithea.model.ShoppingCart;
import com.github.zackhotte.pasithea.model.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

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

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
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

    @PostMapping(path = "/addtocart", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> addToCart(@RequestBody Map<String, String> body, HttpServletRequest request)
            throws OutOfStockException, URISyntaxException {

        long bookId = Long.parseLong(body.get("id"));
        int quantity = Integer.parseInt(body.get("quantity"));

        Book book = bookRepository.findOne(bookId);
        book.subtractQuantity(quantity);
        ShoppingCart cartItem = shoppingCartRepository.save(new ShoppingCart(book, quantity));

        String origin = ServletUriComponentsBuilder.fromContextPath(request).toUriString();
        URI uri = new URI(origin + "/products/shoppingcart/" + cartItem.getId());
        return ResponseEntity.created(uri).body(Response.ok("Item added to your shopping cart: " + uri));
    }

}
