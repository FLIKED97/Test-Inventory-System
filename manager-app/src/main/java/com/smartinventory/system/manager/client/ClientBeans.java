package com.smartinventory.system.manager.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {

    ///Потрібно повертати саме об'єкт що реалізує,
    /// а не інтерфейс який реалізують,
    /// хоча внідряються саме по ньому
    @Bean
    public RestClientProductsRestClient productsRestClient(
           @Value("${inventory.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri){
        return new RestClientProductsRestClient(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .build());
    }
}
