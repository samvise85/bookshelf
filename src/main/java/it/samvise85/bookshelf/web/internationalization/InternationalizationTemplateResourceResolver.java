package it.samvise85.bookshelf.web.internationalization;

import it.samvise85.bookshelf.exception.BookshelfException;
import it.samvise85.bookshelf.web.security.ProfilationTransformer;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;
import org.springframework.web.servlet.resource.TransformedResource;

public class InternationalizationTemplateResourceResolver extends PathResourceResolver {

	ConcurrentMapCache cache = new ConcurrentMapCache("bookshelfcache");
	InternationalizationTransformer languageTransformer = new InternationalizationTransformer();
	ProfilationTransformer profileTransformer = new ProfilationTransformer();
	
	@Override
	protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath, List<? extends Resource> locations,
			ResourceResolverChain chain) {
		if(cache.get(requestPath) == null) {
			String path = requestPath;
			Pattern pattern = Pattern.compile("(_[a-z]+_[0-9]+)");
			Matcher matcher = pattern.matcher(path);
			path = matcher.replaceAll("");
			Resource resource = super.resolveResourceInternal(request, path, locations, chain);
			
			try {
				//add transformed resource to cache
				TransformedResource transformedResource = languageTransformer.transform(request, resource);
				cache.put(requestPath, transformedResource);
			} catch(IOException e) {
				throw new BookshelfException(e.getMessage(), e);
			}
		}
		
		//use cached resource to profilate
		try {
			return profileTransformer.transform(request, (TransformedResource) ((SimpleValueWrapper)cache.get(requestPath)).get());
		} catch(IOException e) {
			throw new BookshelfException(e.getMessage(), e);
		}
	}

}