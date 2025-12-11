package com.smartinventory.system.manager.config;

import com.smartinventory.system.manager.client.RestClientProductsRestClient;
import com.smartinventory.system.manager.security.OAuthClientHttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {

    ///Потрібно повертати саме об'єкт що реалізує,
    /// а не інтерфейс який реалізують,
    /// хоча внідряються саме по ньому
//    @Bean
//    public RestClientProductsRestClient productsRestClient(
//            @Value("${inventory.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri,
//            @Value("${inventory.services.catalogue.username:}") String catalogueBaseUsername,
//            @Value("${inventory.services.catalogue.password:}") String catalogueBasePassword){
//
//        System.out.println("Catalogue URI: " + catalogueBaseUri);
//        System.out.println("Username: " + catalogueBaseUsername);
//        System.out.println("Password: " + (catalogueBasePassword.isEmpty() ? "EMPTY" : "SET"));
//
//        return new RestClientProductsRestClient(RestClient.builder()
//                .baseUrl(catalogueBaseUri)
//                .requestInterceptor(
//                        new BasicAuthenticationInterceptor(catalogueBaseUsername, catalogueBasePassword)) //Для перехоплення аунтифікації
//                .build());
//    }

    @Bean
    public RestClientProductsRestClient productsRestClient(
            @Value("${inventory.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository,
            @Value("${inventory.services.catalogue.registration-id:keycloak}") String registrationId){

        return new RestClientProductsRestClient(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .requestInterceptor(new OAuthClientHttpRequestInterceptor(
                        new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository,
                                authorizedClientRepository), registrationId))
                .build());
    }
}
