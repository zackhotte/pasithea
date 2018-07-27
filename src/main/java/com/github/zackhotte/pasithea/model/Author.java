package com.github.zackhotte.pasithea.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;
    private String fullName;

    private Author() {
    }

    public Author(String firstName, String lastName) {
        this.firstName = capitlize(firstName);
        this.lastName = capitlize(lastName);
        this.fullName = this.firstName + " " + this.lastName;
    }

    private String capitlize(String name) {
        return name.toLowerCase().substring(0, 1).toUpperCase() + name.toLowerCase().substring(1);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return fullName;
    }
}

