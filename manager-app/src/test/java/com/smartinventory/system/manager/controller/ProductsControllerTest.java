package com.smartinventory.system.manager.controller;

import com.smartinventory.system.manager.client.BadRequestException;
import com.smartinventory.system.manager.client.ProductsRestClient;
import com.smartinventory.system.manager.controller.payload.NewProductPayload;
import com.smartinventory.system.manager.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульні тести  ProductController")
class ProductsControllerTest {
    @Mock
    ProductsRestClient productsRestClient;

    @InjectMocks
    ProductsController controller;

    //Потрібно старатися покривати максимальну кількість сценаріїв кода
    @Test
    @DisplayName("createProduct створить новий товар і має перенаправити на сторінку товару")
    void createProduct_RequestIsValid_ReturnsRedirectionToProductPage(){
        //Структура - ААА

        //given
        var payload = new NewProductPayload("Новий товар", "опис нового товару");
        var model = new ConcurrentModel();

        doReturn(new Product(1, "Новий товар", "опис нового товару"))
                .when(this.productsRestClient)
                .createProduct("Новий товар", "опис нового товару");


        //when
        var result = this.controller.createProduct(payload, model);
        //then
        assertEquals("redirect:/catalogue/products/1", result);

        verify(this.productsRestClient).createProduct("Новий товар", "опис нового товару");
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    @DisplayName("createProduct поверне сторінку з помилками, якщо запрос буде не валідним")
    void createProduct_RequestIsInvalid_ReturnsProductFormWithErrors(){
        //Структура - ААА

        //given
        var payload = new NewProductPayload("  ", null);
        var model = new ConcurrentModel();

        doThrow(new BadRequestException(List.of("Помилка 1", "Помилка 2")))
                .when(this.productsRestClient)
                .createProduct("  ", null);

        //when
        var result = this.controller.createProduct(payload, model);

        //then
        assertEquals("catalogue/products/new_product", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Помилка 1", "Помилка 2"), model.getAttribute("errors"));

        verify(this.productsRestClient).createProduct("  ", null);
        verifyNoMoreInteractions(this.productsRestClient);
    }
}