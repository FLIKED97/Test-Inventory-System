package com.smartinventory.system.service;

import com.smartinventory.system.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CachedProductService {
//        implements ProductService {

    private final ProductService delegate;

    private final Map<String, List<Product>> cache = new HashMap<>();

//    @Override
//    public List<Product> findAllProducts() {
//        return cache.computeIfAbsent("all", k -> delegate.findAllProducts());
//    }
}

