package com.github.asyu.homework.domain.persistence.repository;

import com.github.asyu.homework.domain.persistence.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
