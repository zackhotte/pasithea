package com.github.zackhotte.pasithea.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public JsonNode getBooks(@RequestParam(value = "q", required = false, defaultValue = "") String q) {
        ObjectMapper mapper = new ObjectMapper();
        if (q == null || q.isEmpty()) {
            return getAllBooks();
        }
        return searchForBook(q);
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

    @PostMapping(path = "/removefromcart", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> removeFromCart(@RequestBody Map<String, Long> product) {
        List<ShoppingCart> shoppingCart = shoppingCartRepository.findAll();
        if (shoppingCart.isEmpty()) {
            throw new NoSuchElementException("Shopping cart is empty");
        }

        Optional<ShoppingCart> targetItem = shoppingCart.stream()
                .filter(item -> item.getBook().getId().equals(product.get("id")))
                .findFirst();

        if (targetItem.isPresent()) {
            Long cartItemId = targetItem.get().getId();
            shoppingCartRepository.delete(cartItemId);
            return ResponseEntity.ok().body(Response.ok("Book id " + cartItemId + " has been removed from the shopping cart"));
        }

        throw new NoSuchElementException("Could not find book id " + product.get("id"));
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

    private ArrayNode getAllBooks() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode node = mapper.createArrayNode();
        bookRepository.findAll().forEach(book -> {
            ObjectNode object = mapper.createObjectNode();
            object.put("id", book.getId());
            object.put("name", book.getName());
            object.put("category", "BOOK");
            createLink(object, "http://localhost:8080/products/" + book.getId(), "self");
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

}
