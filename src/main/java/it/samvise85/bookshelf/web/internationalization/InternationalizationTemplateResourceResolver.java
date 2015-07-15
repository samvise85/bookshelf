package it.samvise85.bookshelf.web.internationalization;

import it.samvise85.bookshelf.exception.BookshelfException;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

public class InternationalizationTemplateResourceResolver extends PathResourceResolver {

	InternationalizationTransformer transformer = new InternationalizationTransformer();
	
	@Override
	protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath, List<? extends Resource> locations,
			ResourceResolverChain chain) {
		String path = requestPath;
		Pattern pattern = Pattern.compile("(_[a-z]+_[0-9]+)");
		Matcher matcher = pattern.matcher(path);
		path = matcher.replaceAll("");
		Resource resource = super.resolveResourceInternal(request, path, locations, chain);
		
		try {
			return transformer.transform(request, resource);
		} catch(IOException e) {
			throw new BookshelfException(e.getMessage(), e);
		}
	}

}