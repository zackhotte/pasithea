package com.github.zackhotte.pasithea.controller;

import com.github.zackhotte.pasithea.Application;
import com.github.zackhotte.pasithea.repositories.AuthorRepository;
import com.github.zackhotte.pasithea.repositories.BookRepository;
import com.github.zackhotte.pasithea.repositories.ShoppingCartRepository;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShoppingCartRestControllerTest {

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
    public void test1ShoppingCartStartsEmpty() throws Exception {
        mockMvc.perform(get("/api/shoppingcart"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string("[]"));
    }


    @Test
    public void test2ThatItemHasBeenAddedToShoppingCart() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String origin = request.getRequestURL().toString();
        String body = "{\"id\": \"3\", \"quantity\": \"2\"}";

        mockMvc.perform(post("/api/shoppingcart/addtocart")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("Item id 3 has been added to your shopping cart")))
                .andExpect(jsonPath("$.link", is(origin + "/products/shoppingcart/1")));

        mockMvc.perform(get("/api/shoppingcart/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void test3ThatOutOfStockErrorIsThrown() throws Exception {
        String body = "{\"id\": \"19\", \"quantity\": \"500\"}";
        mockMvc.perform(post("/api/shoppingcart/addtocart")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Out of Stock")));
    }

    @Test
    public void test4ThatErrorIsThrownIfItemIdDoesNotExist() throws Exception {
        String body = "{\"id\": \"200\"}";
        mockMvc.perform(post("/api/shoppingcart/removefromcart")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Could not find product id 200")))
                .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    public void test5ThatItemIsRemoved() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String origin = request.getRequestURL().toString();
        String body = "{\"id\": \"3\"}";

        mockMvc.perform(post("/api/shoppingcart/removefromcart")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Product id 3 has been removed from the shopping cart")))
                .andExpect(jsonPath("$.link", is(origin + "/products/shoppingcart")));
        ;

        mockMvc.perform(get("/api/shoppingcart"))
                .andExpect(content().contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void test6ThatTotalPriceWasCorrectlyCalculated() throws Exception {
        String body = "{\"id\": \"55\", \"quantity\": \"5\"}";
        mockMvc.perform(post("/api/shoppingcart/addtocart")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/shoppingcart/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.unitPrice", is(15.21)))
                .andExpect(jsonPath("$.quantity", is(5)))
                .andExpect(jsonPath("$.totalPrice", is(76.05)));
    }

    @Test
    public void test7ThatErrorIsThrownWhenItemIdDoesNotExistWhenAddingToCart() throws Exception {
        String body = "{\"id\": \"2000\", \"quantity\": \"1\"}";
        mockMvc.perform(post("/api/shoppingcart/addtocart")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.error", is("Could not find product id 2000")));
    }

}