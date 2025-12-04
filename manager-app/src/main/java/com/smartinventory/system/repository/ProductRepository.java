package com.smartinventory.system.repository;

import com.smartinventory.system.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll();

    Product save(Product product);

    Optional<Product> findById(Integer productId);

    void delete(Integer id);
}
