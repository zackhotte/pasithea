package com.github.zackhotte.pasithea.model;

public class OutOfStockException extends Throwable {
    public OutOfStockException(Long id) {
        super("Not enough inventory for product id " + id);
    }
}
