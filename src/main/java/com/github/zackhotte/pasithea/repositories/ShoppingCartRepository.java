package com.github.zackhotte.pasithea.repositories;

import com.github.zackhotte.pasithea.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
}
