package com.smartinventory.system.catalogue.repository;

import com.smartinventory.system.catalogue.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = "classpath:sql/products.sql", config = @SqlConfig(encoding = "UTF-8"))
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryIT {

    static {
        // Встановлюємо зону UTC, щоб JDBC драйвер не передавав "Europe/Kiev"
        System.setProperty("user.timezone", "UTC");
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
    }

    @Autowired
    ProductRepository productRepository;

    @Test
    void findAllByTitleLikeIgnoreCase_ReturnsFilteredProductsList(){
        //given
        var filter = "%морозиво%";
        //when
        var products = this.productRepository.findAllByTitleLikeIgnoreCase(filter);
        //then
        assertEquals(List.of(new Product(5,"Морозиво", "Дуже смачне морозиво")), products);
    }

}