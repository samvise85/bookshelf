package it.samvise85.bookshelf.rest.main;

import it.samvise85.bookshelf.rest.security.config.SpringSecurityConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages={"it.samvise85.bookshelf"})
//@Import(SpringSecurityConfig.class)
@EnableAutoConfiguration
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}