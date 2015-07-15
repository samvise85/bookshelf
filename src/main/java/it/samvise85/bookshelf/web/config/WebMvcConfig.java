package it.samvise85.bookshelf.web.config;

import it.samvise85.bookshelf.web.internationalization.InternationalizationResourceResolver;
import it.samvise85.bookshelf.web.internationalization.InternationalizationTemplateResourceResolver;
import it.samvise85.bookshelf.web.internationalization.TimestampResourceResolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
		String resourceLocations = "classpath:static/";
		ConcurrentMapCache cache = new ConcurrentMapCache("bookshelfcache");
		CachingResourceResolver cacheResolver = new CachingResourceResolver(cache);
		
		registry.addResourceHandler("/**")
				.addResourceLocations(resourceLocations)
				.resourceChain(true)
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
}