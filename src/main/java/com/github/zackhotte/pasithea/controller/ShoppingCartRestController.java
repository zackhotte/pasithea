package com.github.zackhotte.pasithea.controller;

import com.github.zackhotte.pasithea.model.Book;
import com.github.zackhotte.pasithea.model.OutOfStockException;
import com.github.zackhotte.pasithea.model.Response;
import com.github.zackhotte.pasithea.model.ShoppingCart;
import com.github.zackhotte.pasithea.repositories.AuthorRepository;
import com.github.zackhotte.pasithea.repositories.BookRepository;
import com.github.zackhotte.pasithea.repositories.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/shoppingcart")
public class ShoppingCartRestController {

    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    public ShoppingCartRestController(BookRepository bookRepository, AuthorRepository authorRepository,
                                 ShoppingCartRepository shoppingCartRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.shoppingCartRepository = shoppingCartRepository;
    }

    @GetMapping
    public List<ShoppingCart> shoppingCart() {
        return shoppingCartRepository.findAll();
    }

    @GetMapping("/{shoppingCartId}")
    public ShoppingCart getItemInShoppingCart(@PathVariable Long shoppingCartId) {
        return shoppingCartRepository.findOne(shoppingCartId);
    }

    @PostMapping(path = "/addtocart", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> addToCart(@RequestBody Map<String, String> body, HttpServletRequest request)
            throws OutOfStockException, URISyntaxException {

        long bookId = Long.parseLong(body.get("id"));
        int quantity = Integer.parseInt(body.get("quantity"));

        Validator.validateProductId(bookId);
        Book book = bookRepository.findOne(bookId);
        book.subtractQuantity(quantity);
        ShoppingCart cartItem = shoppingCartRepository.save(new ShoppingCart(book, quantity));

        String origin = ServletUriComponentsBuilder.fromContextPath(request).toUriString();
        URI uri = new URI(origin + "/api/shoppingcart/" + cartItem.getId());
        return ResponseEntity.created(uri).body(Response.ok(
                "Product id " + bookId + " has been added to your shopping cart", uri.toString()
        ));
    }

    @PostMapping(path = "/removefromcart", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> removeFromCart(@RequestBody Map<String, Long> product, HttpServletRequest request) {
        List<ShoppingCart> shoppingCart = shoppingCartRepository.findAll();
        if (shoppingCart.isEmpty()) {
            throw new NoSuchElementException("Shopping cart is empty");
        }

        Long productId = product.get("id");
        Optional<ShoppingCart> targetItem = shoppingCart.stream()
                .filter(item -> item.getBook().getId().equals(product.get("id")))
                .findFirst();

        if (targetItem.isPresent()) {
            Long cartItemId = targetItem.get().getId();
            shoppingCartRepository.delete(cartItemId);
            String origin = ServletUriComponentsBuilder.fromContextPath(request).toUriString();
            return ResponseEntity.ok().body(Response.ok(
                    "Product id " + productId + " has been removed from the shopping cart",
                    origin + "/api/shoppingcart"
            ));
        }

        throw new NoSuchElementException("Could not find product id " + product.get("id"));
    }


}
