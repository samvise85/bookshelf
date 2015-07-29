package it.samvise85.bookshelf.web.config;

import it.samvise85.bookshelf.web.internationalization.InternationalizationResourceResolver;
import it.samvise85.bookshelf.web.internationalization.InternationalizationTemplateResourceResolver;
import it.samvise85.bookshelf.web.internationalization.InternationalizationTransformer;
import it.samvise85.bookshelf.web.internationalization.TimestampResourceResolver;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	
	@Autowired
	private Environment env;

	@Autowired
	private ResourceUrlProvider urlProvider;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String resourceLocations = "classpath:static/";
		
		registry.addResourceHandler("/**")
				.addResourceLocations(resourceLocations)
				.resourceChain(true)
				.addResolver(new TimestampResourceResolver())
				.addResolver(new InternationalizationResourceResolver())
				.addResolver(new InternationalizationTemplateResourceResolver(
						new BookshelfTransformerChain()
						.add(new VersionTransformer())
						.add(new InternationalizationTransformer())
				))
				;
		super.addResourceHandlers(registry);
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
	    registry.addViewController("/").setViewName("forward:/index.html");
	}

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		return new MultipartConfigElement("", 1024*1024, 1200*1024, 1024*1024);
	}

	@Bean
	public MultipartResolver multipartResolver() {
		org.springframework.web.multipart.commons.CommonsMultipartResolver multipartResolver = new org.springframework.web.multipart.commons.CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(1000000);
		return multipartResolver;
	}
}