package com.smartinventory.system.catalogue;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SecurityScheme(
        name = "keycloak",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(authorizationCode = @OAuthFlow(
                authorizationUrl = "${keycloak.uri}/realms/inventory-test/protocol/openid-connect/auth",
                tokenUrl = "${keycloak.uri}/realms/inventory-test/protocol/openid-connect/token",
                scopes = {
                        @OAuthScope(name = "openid"),
                        @OAuthScope(name = "microprofile-jwt"),
                        @OAuthScope(name = "edit_catalogue"),
                        @OAuthScope(name = "view_catalogue")
                }
        ))
)
@SpringBootApplication
public class CatalogueServiceApp {
    public static void main( String[] args ) {
        //System.out.println(TimeZone.getDefault().getID());
        //TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kyiv"));
        SpringApplication.run(CatalogueServiceApp.class, args);
    }
}
