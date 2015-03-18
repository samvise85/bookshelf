package it.samvise85.bookshelf.rest.config;

//import java.util.List;

//import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//import org.springframework.web.servlet.resource.ResourceResolver;
//import org.springframework.web.servlet.resource.ResourceResolverChain;

@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");//.resourceChain(true).addResolver(new MyResourceResolver());
	}
	
//	 private static class MyResourceResolver implements ResourceResolver {
//		
//		@Override
//		public String resolveUrlPath(String resourcePath,
//				List<? extends Resource> locations, ResourceResolverChain chain) {
//			if("/".equals(resourcePath))
//				resourcePath = "/index.html";
//			return resourcePath;
//		}
//		
//		@Override
//		public Resource resolveResource(HttpServletRequest request,
//				String requestPath, List<? extends Resource> locations,
//				ResourceResolverChain chain) {
//			if("/".equals(requestPath))
//				requestPath = "/index.html";
//			return chain.resolveResource(request, requestPath, locations);
//		}
//	}
}