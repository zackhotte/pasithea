package com.github.zackhotte.pasithea;

import com.github.zackhotte.pasithea.model.Author;
import com.github.zackhotte.pasithea.model.AuthorRepository;
import com.github.zackhotte.pasithea.model.Book;
import com.github.zackhotte.pasithea.model.BookRepository;
import com.github.zackhotte.pasithea.model.Format;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataLoader implements CommandLineRunner {

    private BookRepository bookRepository;
    private AuthorRepository authorRepository;

    private Map<String, Author> authors = new HashMap<>();
    private List<Book> books = new ArrayList<>();

    @Autowired
    public DataLoader(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        readAndParseBooks();
    }

    @Override
    public void run(String... args) throws Exception {
        authors.forEach((key, value) -> authorRepository.save(value));
        books.forEach(book -> bookRepository.save(book));
    }

    private void readAndParseBooks() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            String path = URLDecoder.decode(classLoader.getResource("data/books.csv").getFile(), "UTF-8");

            File file = new File(path);
            Reader in = new FileReader(file);
            CSVParser records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            setAuthorsAndBook(records);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setAuthorsAndBook(CSVParser records) {
        for (CSVRecord record : records) {
            String authorName = record.get("full_name");

            if (authors.get(authorName) == null) {
                authors.put(authorName, new Author(
                        record.get("first_name").trim(),
                        record.get("last_name").trim()
                ));
            }

            Book book = new Book(
                    record.get("original_title"),
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris semper.",
                    record.get("publisher_name"),
                    parseDate(record.get("date")),
                    Format.PAPERBACK,
                    Integer.valueOf(record.get("quantity")),
                    Float.valueOf(record.get("price")),
                    record.get("image_url"),
                    Integer.valueOf(record.get("page_count")),
                    record.get("original_publication_year"),
                    Float.valueOf(record.get("average_rating"))
            );

            book.getAuthors().add(authors.get(authorName));
            books.add(book);
        }
    }

    private Date parseDate(String date) {
        try {
            return new SimpleDateFormat("MMM d yyyy").parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

}
