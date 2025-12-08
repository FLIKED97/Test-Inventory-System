package com.smartinventory.system.catalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class CatalogueServiceApp {
    public static void main( String[] args ) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kyiv"));
        SpringApplication.run(CatalogueServiceApp.class, args);
    }
}
