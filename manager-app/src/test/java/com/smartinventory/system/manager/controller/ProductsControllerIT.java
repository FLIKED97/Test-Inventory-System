package com.smartinventory.system.manager.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.smartinventory.system.manager.entity.Product;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.flyway.enabled=false")
@AutoConfigureMockMvc
//@AutoConfigureTestDatabase
@WireMockTest(httpPort = 54321)
public class ProductsControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getProductsList_ReturnsProductsListPage()throws Exception{
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/list")
                .queryParam("filter", "товар")
                .with(user("FLIKED97").roles("MANAGER"));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/products/list"))
                .withQueryParam("filter", WireMock.equalTo("товар"))
                .willReturn(WireMock.ok("""
                        [
                        {"id": 1, "title": "Товар №1", "details": "Опис №1"},
                        {"id": 2, "title": "Товар №2", "details": "Опис №2"}
                        ]
                """).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        //when
        this.mockMvc.perform(requestBuilder)
        //than
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/list"),
                        model().attribute("filter", "товар"),
                        model().attribute("products", List.of(
                                new Product(1, "Товар №1", "Опис №1"),
                                new Product(2, "Товар №2", "Опис №2")
                        ))

                );
        WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching("/catalogue-api/products/list"))
                .withQueryParam("filter", WireMock.equalTo("товар")));
    }

    @Test
    void getNewProductPage_ReturnsProductPage() throws Exception{
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/create")
                .with(user("FLIKED97").roles("MANAGER"));
        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/new_product")
                );
    }
}
