package com.github.zackhotte.pasithea.repositories;

import com.github.zackhotte.pasithea.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
