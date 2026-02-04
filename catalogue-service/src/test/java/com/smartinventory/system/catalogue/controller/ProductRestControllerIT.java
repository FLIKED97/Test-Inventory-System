package com.smartinventory.system.catalogue.controller;


//import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import com.smartinventory.system.catalogue.entity.Product;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(ProductRestController.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(RestDocumentationExtension.class)
class ProductRestControllerIT {

    static {
        // Встановлюємо зону UTC, щоб JDBC драйвер не передавав "Europe/Kiev"
        System.setProperty("user.timezone", "UTC");
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
    }

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Sql(scripts = "/sql/products.sql", config = @SqlConfig(encoding = "UTF-8"))
    void getProduct_ShouldHaveProductInModel_WhenProductExists() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products/{productId}", 1)
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));
        //when
        this.mockMvc.perform(requestBuilder)
        //than
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                        {
                        "id": 1,
                        "title": "Товар №1",
                        "details": "Опис товару №1"
                        }
                        
                        """)
                );

    }
    @Test
    void getProduct_ShouldReturn404_WhenProductNotFound() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products/{productId}", 999)
                .locale(Locale.forLanguageTag("uk-UA"))
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));
        //when
        this.mockMvc.perform(requestBuilder)
        //than
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$.detail").value("Товар не знайдено")
                );
    }

    @Test
    @Sql(scripts = "/sql/products.sql", config = @SqlConfig(encoding = "UTF-8"))
    void updateProduct_ValidPayload_UpdatesProductAndReturnsNoContent() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders
                .patch("/catalogue-api/products/{productId}", 1)
                .locale(Locale.forLanguageTag("uk-UA"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
            {"title": "Змінено тайтл", "details": "Змінено опис"}""")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        // when
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isNoContent());

        // then - verify persistence
        entityManager.flush();
        entityManager.clear();

        var product = entityManager.find(Product.class, 1);

        assertThat(product)
                .isNotNull()
                .extracting(Product::getTitle, Product::getDetails)
                .containsExactly("Змінено тайтл", "Змінено опис");
    }
    @Test
    @Sql(scripts = "/sql/products.sql", config = @SqlConfig(encoding = "UTF-8"))
    void updateProduct_InvalidPayloadWithBindException_ThrowsBindException() throws Exception{
        // given
        var requestBuilder = MockMvcRequestBuilders
                .patch("/catalogue-api/products/{productId}", 1)
                .locale(Locale.forLanguageTag("uk-UA"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
            {"title": "Зм", "details": "null"}""")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        // when
        this.mockMvc.perform(requestBuilder)
        //than
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                        {
                          "errors": [
                          "Розмір назви повинен бути від 3 до 50 символів"
                          ]
                        }
                        """)
                );

    }

}