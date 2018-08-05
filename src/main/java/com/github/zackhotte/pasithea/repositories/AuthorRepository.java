package com.github.zackhotte.pasithea.repositories;

import com.github.zackhotte.pasithea.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
