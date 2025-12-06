package com.smartinventory.system.manager.client;

import com.smartinventory.system.manager.controller.payload.NewProductPayload;
import com.smartinventory.system.manager.controller.payload.UpdateProductPayload;
import com.smartinventory.system.manager.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
public class RestClientProductsRestClient implements ProductsRestClient {

    private static final ParameterizedTypeReference<List<Product>> PRODUCTS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    @Override
    public List<Product> findAllProducts() {
        return this.restClient
                .get()
                .uri("/catalogue-api/products/list")
                .retrieve()
                .body(PRODUCTS_TYPE_REFERENCE);
    }

    @Override
    public Product createProduct(String title, String details) {
        try {
            return this.restClient
                    .post()
                    .uri("catalogue-api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new NewProductPayload(title, details))
                    .retrieve()
                    .body(Product.class);
        } catch (HttpClientErrorException.BadRequest exception){
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
            // ТРЕБА РОБИТИ ПЕРЕВІРКИ, ЩО ПРОПЕРТІС НЕ НУЛ, ЩО ТАМ Є КЛЮЧ ЕРРОРС І ВІН ЯВЛЯЄТЬСЯ СПИСКОМ РЯДКІВ
        }
    }

    @Override
    public Optional<Product> findProduct(int productId) {
        try {

            return Optional.ofNullable(this.restClient.get()
                    .uri("catalogue-api/products/{productId}", productId)
                    .retrieve()
                    .body(Product.class));
        } catch (HttpClientErrorException.NotFound exception){
            return Optional.empty();
        }
    }

    @Override
    public void updateProduct(int product, String title, String details) {
        try {
             this.restClient
                    .patch()
                    .uri("catalogue-api/products/{productId}", product)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateProductPayload(title, details))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest exception){
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
            // ТРЕБА РОБИТИ ПЕРЕВІРКИ, ЩО ПРОПЕРТІС НЕ НУЛ, ЩО ТАМ Є КЛЮЧ ЕРРОРС І ВІН ЯВЛЯЄТЬСЯ СПИСКОМ РЯДКІВ
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try {

             this.restClient.delete()
                    .uri("catalogue-api/products/{productId}", productId)
                    .retrieve()
                    .body(Product.class);
        } catch (HttpClientErrorException.NotFound exception){
            throw new NoSuchElementException(exception);
        }
    }
}
