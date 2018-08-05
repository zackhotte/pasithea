package com.github.zackhotte.pasithea.controller;

import com.github.zackhotte.pasithea.Application;
import com.github.zackhotte.pasithea.model.AuthorRepository;
import com.github.zackhotte.pasithea.model.BookRepository;
import com.github.zackhotte.pasithea.model.ShoppingCartRepository;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductRestControllerTest {

    private MockMvc mockMvc;
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void test1ProductInformation() throws Exception {
        mockMvc.perform(get("/products/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.name", is("The Great Gatsby")))
                .andExpect(jsonPath("$.quantity", is(438)))
                .andExpect(jsonPath("$.inStock", is(true)));

        mockMvc.perform(get("/products/80"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(80)))
                .andExpect(jsonPath("$.name", is("Le Petit Prince")))
                .andExpect(jsonPath("$.quantity", is(0)))
                .andExpect(jsonPath("$.inStock", is(false)));
    }

    @Test
    public void test2ShoppingCartStartsEmpty() throws Exception {
        mockMvc.perform(get("/products/shoppingcart"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string("[]"));
    }

    @Test
    public void test3ThatItemHasBeenAddedToShoppingCart() throws Exception {
        String body = "{\"id\": \"3\", \"quantity\": \"2\"}";
        mockMvc.perform(post("/products/addtocart")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/products/shoppingcart/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void test4ThatOutOfStockErrorIsThrown() throws Exception {
        String body = "{\"id\": \"19\", \"quantity\": \"500\"}";
        mockMvc.perform(post("/products/addtocart")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Out of Stock")));
    }

    @Test
    public void test5ThatErrorIsThrownIfItemIdDoesNotExist() throws Exception {
        String body = "{\"id\": \"200\"}";
        mockMvc.perform(post("/products/removefromcart")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Could not find book id 200")))
                .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    public void test6ThatItemIsRemoved() throws Exception {
        String body = "{\"id\": \"3\"}";
        mockMvc.perform(post("/products/removefromcart")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Book id 1 has been removed from the shopping cart")));

        mockMvc.perform(get("/products/shoppingcart"))
                .andExpect(content().contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void test7ThatTotalPriceWasCorrectlyCalculated() throws Exception {
        String body = "{\"id\": \"55\", \"quantity\": \"5\"}";
        mockMvc.perform(post("/products/addtocart")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/products/shoppingcart/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.unitPrice", is(15.21)))
                .andExpect(jsonPath("$.quantity", is(5)))
                .andExpect(jsonPath("$.totalPrice", is(76.05)));
    }

    @Test
    public void test8ProductSearchByName() throws Exception {
        mockMvc.perform(get("/products?q=Harry Potter"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.q", is("Harry Potter")))
                .andExpect(jsonPath("$.res.numFound", is(7)))
                .andExpect(jsonPath("$.res.results", hasSize(7)))
                .andExpect(jsonPath("$.res.results[0].name", is("Harry Potter and the Philosopher's Stone")));
    }

}