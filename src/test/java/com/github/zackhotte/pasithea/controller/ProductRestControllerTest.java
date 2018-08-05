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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        mockMvc.perform(get("/api/products/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.name", is("The Great Gatsby")))
                .andExpect(jsonPath("$.quantity", is(438)))
                .andExpect(jsonPath("$.inStock", is(true)));

        mockMvc.perform(get("/api/products/80"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(80)))
                .andExpect(jsonPath("$.name", is("Le Petit Prince")))
                .andExpect(jsonPath("$.quantity", is(0)))
                .andExpect(jsonPath("$.inStock", is(false)));
    }

    @Test
    public void test2ProductSearchByName() throws Exception {
        mockMvc.perform(get("/api/products?q=Harry Potter"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.q", is("Harry Potter")))
                .andExpect(jsonPath("$.res.numFound", is(7)))
                .andExpect(jsonPath("$.res.results", hasSize(7)))
                .andExpect(jsonPath("$.res.results[0].name", is("Harry Potter and the Philosopher's Stone")));
    }

    @Test
    public void test3ThatErrorIsThrownWhenItemIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/products/2000"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.error", is("Could not find product id 2000")));
    }


}