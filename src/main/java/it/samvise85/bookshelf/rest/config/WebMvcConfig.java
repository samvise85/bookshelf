package it.samvise85.bookshelf.rest.config;

import it.samvise85.bookshelf.exception.BookshelfException;
import it.samvise85.bookshelf.rest.support.InternationalizationResourceResolver;
import it.samvise85.bookshelf.rest.support.InternationalizationTemplateResourceResolver;
import it.samvise85.bookshelf.rest.support.TimestampResourceResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.CachingResourceResolver;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	
	@Autowired
	private Environment env;

	@Autowired
	private ResourceUrlProvider urlProvider;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		boolean devMode = this.env.acceptsProfiles("development");

		boolean useResourceCache = !devMode;
		String resourceLocations = "classpath:static/";
		if(devMode)
			resourceLocations = "file:///C:/Progetti/Bookshelf/workspace/bookshelf/src/main/resources/static/";
		ConcurrentMapCache cache = new ConcurrentMapCache("bookshelfcache");
		CachingResourceResolver cacheResolver = new CachingResourceResolver(cache);
		
		registry.addResourceHandler("/**")
				.addResourceLocations(resourceLocations)
				.resourceChain(useResourceCache)
				.addResolver(new TimestampResourceResolver())
				.addResolver(new InternationalizationResourceResolver())
				.addResolver(cacheResolver)
				.addResolver(new InternationalizationTemplateResourceResolver())
				;
		super.addResourceHandlers(registry);
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
	    registry.addViewController("/").setViewName("forward:/index.html");
	}

	@Bean
	public JavaMailSenderImpl javaMailSenderImpl() {
		String dataDir = System.getenv("OPENSHIFT_DATA_DIR");
		if(dataDir == null)
			dataDir = System.getProperty("user.home") + File.separator;
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(dataDir + "mail.properties"));
			JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
			mailSenderImpl.setHost(props.getProperty("smtp.host"));
			mailSenderImpl.setPort(Integer.parseInt(props.getProperty("smtp.port")));
			mailSenderImpl.setProtocol(props.getProperty("smtp.protocol"));
			mailSenderImpl.setUsername(props.getProperty("smtp.username"));
			mailSenderImpl.setPassword(props.getProperty("smtp.password"));
			Properties javaMailProps = new Properties();
			javaMailProps.put("mail.smtp.auth", true);
			javaMailProps.put("mail.smtp.starttls.enable", true);
			mailSenderImpl.setJavaMailProperties(javaMailProps);
			return mailSenderImpl;
		} catch(FileNotFoundException e) {
			return null;
		} catch(IOException e) {
			throw new BookshelfException(e.getMessage(), e);
		}
		
	}
}