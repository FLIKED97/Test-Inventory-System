package com.smartinventory.system.repository;

import com.smartinventory.system.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.IntStream;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final List<Product> products = Collections.synchronizedList(new LinkedList<>());


    @Override
    public List<Product> findAll() {
        return Collections.unmodifiableList(this.products);
    }

    @Override
    public Product save(Product product) {
       product.setId(this.products.stream()
               .max(Comparator.comparingInt(Product::getId))
               .map(Product::getId)
               .orElse(0) + 1);
       this.products.add(product);
        return product;
    }

    @Override
    public Optional<Product> findById(Integer productId) {
        return this.products.stream()
                .filter(i -> Objects.equals(i.getId(), productId))
                .findFirst();
    }

    @Override
    public void delete(Integer id) {
      this.products.removeIf(i -> Objects.equals(i.getId(), id));
    }
}
