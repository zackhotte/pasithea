package com.github.zackhotte.pasithea.controller;

import com.github.zackhotte.pasithea.Application;
import com.github.zackhotte.pasithea.model.Author;
import com.github.zackhotte.pasithea.model.AuthorRepository;
import com.github.zackhotte.pasithea.model.Book;
import com.github.zackhotte.pasithea.model.BookRepository;
import com.github.zackhotte.pasithea.model.Format;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
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

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Author author1 = authorRepository.save(new Author("Michael", "Lewis"));
        Author author2 = authorRepository.save(new Author("Greg", "Sestero"));
        Author author3 = authorRepository.save(new Author("Tom", "Bissell"));

        Book book1 = new Book(
                "Moneyball: The Art Of Winning An Unfair Game",
                "With these words Michael Lewis launches us into the funniest, smartest, and most contrarian book since, well, since Liar's Poker. Moneyball is a quest for something as elusive as the Holy Grail, something that money apparently can't buy: the secret of success in baseball. The logical places to look would be the front offices of major league teams, and the dugouts, perhaps even in the minds of the players themselves. Lewis mines all these possibilities—his intimate and original portraits of big league ballplayers are alone worth the price of admission—but the real jackpot is a cache of numbers—numbers!—collected over the years by a strange brotherhood of amateur baseball enthusiasts: software engineers, statisticians, Wall Street analysts, lawyers and physics professors.",
                "WW Norton",
                new Date(),
                Format.PAPERBACK,
                50,
                25.25f,
                "http:localhost:8080/products/1"
        );

        Book book2 = new Book(
                "The Disaster Artist: My Life Inside The Room, the Greatest Bad Movie Ever",
                "From the actor who somehow lived through it all, a “sharply detailed…funny book about a cinematic comedy of errors” (The New York Times): the making of the cult film phenomenon The Room.In 2003, an independent film called The Room—starring and written, produced, and directed by a mysteriously wealthy social misfit named Tommy Wiseau—made its disastrous debut in Los Angeles. Described by one reviewer as “like getting stabbed in the head,” the $6 million film earned a grand total of $1,800 at the box office and closed after two weeks. Ten years later, it’s an international cult phenomenon, whose legions of fans attend screenings featuring costumes, audience rituals, merchandising, and thousands of plastic spoons. Hailed by The Huffington Post as “possibly the most important piece of literature ever printed,” The Disaster Artist is the hilarious, behind-the-scenes story of a deliciously awful cinematic phenomenon as well as the story of an odd and inspiring Hollywood friendship. Greg Sestero, Tommy’s costar, recounts the film’s bizarre journey to infamy, explaining how the movie’s many nonsensical scenes and bits of dialogue came to be and unraveling the mystery of Tommy Wiseau himself. But more than just a riotously funny story about cinematic hubris, “The Disaster Artist is one of the most honest books about friendship I’ve read in years” (Los Angeles Times).",
                "Simon & Schuster",
                new Date(),
                Format.HARDCOVER,
                0,
                18.00f,
                "http:localhost:8080/products/2"
        );

        book1.getAuthors().add(author1);
        book2.getAuthors().add(author2);
        book2.getAuthors().add(author3);

        authorRepository.save(author1);
        authorRepository.save(author2);
        authorRepository.save(author3);
        bookRepository.save(book1);
        bookRepository.save(book2);
    }

    @Test
    public void productInformation() throws Exception {
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Moneyball: The Art Of Winning An Unfair Game")))
                .andExpect(jsonPath("$.quantity", is(50)))
                .andExpect(jsonPath("$.inStock", is(true)));

        mockMvc.perform(get("/products/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("The Disaster Artist: My Life Inside The Room, the Greatest Bad Movie Ever")))
                .andExpect(jsonPath("$.quantity", is(0)))
                .andExpect(jsonPath("$.inStock", is(false)));
    }
}