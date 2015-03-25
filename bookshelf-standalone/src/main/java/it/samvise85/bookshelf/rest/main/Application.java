package it.samvise85.bookshelf.rest.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages={"it.samvise85.bookshelf"})
@EnableAutoConfiguration
@SpringBootApplication
@Deprecated
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}