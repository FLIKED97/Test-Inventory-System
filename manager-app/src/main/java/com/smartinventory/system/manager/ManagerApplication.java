package com.smartinventory.system.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class ManagerApplication {
    public static void main(String[] args) {
        //TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kyiv"));
        SpringApplication.run(ManagerApplication.class, args);
    }
}
