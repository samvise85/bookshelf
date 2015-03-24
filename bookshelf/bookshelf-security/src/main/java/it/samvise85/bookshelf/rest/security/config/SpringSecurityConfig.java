package it.samvise85.bookshelf.rest.security.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages={"it.samvise85.bookshelf"})
@ImportResource({ "classpath:webSecurityConfig.xml" })
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	public static final String USERNAME_PARAM_NAME = "bookshelf-username";
	public static final String TOKEN_PARAM_NAME = "bookshelf-token";

	public SpringSecurityConfig() {
	}
}
