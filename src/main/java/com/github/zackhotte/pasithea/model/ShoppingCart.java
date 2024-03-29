package com.github.zackhotte.pasithea.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "shopping_cart")
public class ShoppingCart {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Book book;
    private Integer quantity;
    private Float unitPrice;
    private Float totalPrice;
    private Boolean isOrdered;
    private Boolean hasShipped;
    private Boolean hasBeenDelivered;

    private ShoppingCart() {
    }

    public ShoppingCart(Book book, Integer quantity) {
        this.book = book;
        this.quantity = quantity;
        this.unitPrice = book.getPrice();
        this.totalPrice = this.quantity * this.unitPrice;
        this.isOrdered = false;
        this.hasShipped = false;
        this.hasBeenDelivered = false;
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Float getUnitPrice() {
        return unitPrice;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    public Boolean getOrdered() {
        return isOrdered;
    }

    public Boolean getHasShipped() {
        return hasShipped;
    }

    public Boolean getHasBeenDelivered() {
        return hasBeenDelivered;
    }

}
