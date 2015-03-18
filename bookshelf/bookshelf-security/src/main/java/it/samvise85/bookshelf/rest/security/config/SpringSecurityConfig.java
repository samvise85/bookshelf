package it.samvise85.bookshelf.rest.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({ "classpath:webSecurityConfig.xml" })
public class SpringSecurityConfig {
	public static final String USERNAME_PARAM_NAME = "bookshelf-username";
	public static final String TOKEN_PARAM_NAME = "bookshelf-token";

	public SpringSecurityConfig() {
		super();
	}

}
