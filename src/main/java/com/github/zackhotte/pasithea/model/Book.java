package com.github.zackhotte.pasithea.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product")
public class Book {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;
    @Lob
    private String description;
    private String publisher;
    private Date publishedDate;
    @Enumerated(EnumType.STRING)
    private Format format;
    private Integer quantity;
    private Float price;
    private String url;

    @ManyToMany
    @JoinTable(name = "book_author",
            joinColumns = {@JoinColumn(name = "product", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "author", referencedColumnName = "id")}
    )
    private Set<Author> authors = new HashSet<>();

    private Boolean inStock;

    private Book() {
    }

    public Book(String name, String description, String publisher, Date publishedDate,
                Format format, Integer quantity, Float price, String url) {
        this.name = name;
        this.description = description;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.format = format;
        this.quantity = quantity;
        this.price = price;
        this.url = url;
        verifyInStock();
    }

    public void verifyInStock() {
        inStock = quantity > 0;
    }

    public void subtractQuantity(int amount) throws OutOfStockException {
        int testQuantity = quantity;
        if (!inStock || (testQuantity - amount) <= -1) {
            throw new OutOfStockException(getId());
        }
        quantity -= amount;
        verifyInStock();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPublisher() {
        return publisher;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public Format getFormat() {
        return format;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Float getPrice() {
        return price;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public Set<Author> getAuthors() {
        return authors;
    }
}
