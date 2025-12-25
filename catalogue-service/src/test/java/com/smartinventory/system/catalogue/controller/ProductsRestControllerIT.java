package com.smartinventory.system.catalogue.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductsRestControllerIT {

    static {
        // Встановлюємо зону UTC, щоб JDBC драйвер не передавав "Europe/Kiev"
        System.setProperty("user.timezone", "UTC");
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql(scripts = "/sql/products.sql", config = @SqlConfig(encoding = "UTF-8"))
    void findProducts_ReturnsProductsList()throws Exception{
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products/list")
                .param("filter", "товар")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));

        //when
        this.mockMvc.perform(requestBuilder)
        //than
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                        [
                        {"id": 1, "title": "Товар №1", "details": "Опис товару №1"},
                        {"id": 3, "title": "Товар №3", "details": "Опис товару №3"},
                        {"id": 4, "title": "Товар №4", "details": "Опис товару №4"}
                        ]""")
                );
    }
    @Test
    void createProduct_RequestIsValid_ReturnsNewProduct() throws Exception{
        //given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                       {"title": "Ще один новий товар", "details": "Опис ще одного нового товару"}""")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));
        //when
        this.mockMvc.perform(requestBuilder)
        //than
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        header().string(HttpHeaders.LOCATION, "http://localhost/catalogue-api/products/1"),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                        {"id": 1,
                        "title": "Ще один новий товар", 
                        "details": "Опис ще одного нового товару"}
                        """)
                );
    }
    @Test
    void createProduct_RequestIsInvalid_ReturnsProblemDetails() throws Exception{
        //given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                       {"title": "  ", "details": null}""")
                .locale(Locale.forLanguageTag("uk-UA"))
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));
        //when
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
    @Test
    void createProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception{
        //given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                       {"title": "  ", "details": null}""")
                .locale(Locale.forLanguageTag("uk-UA"))
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));
        //when
        this.mockMvc.perform(requestBuilder)
                //than
                .andDo(print())
                .andExpectAll(
                        status().isForbidden());
    }
}