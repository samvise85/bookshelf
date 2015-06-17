package it.samvise85.bookshelf.rest.support;

import it.samvise85.bookshelf.context.ServiceLocator;
import it.samvise85.bookshelf.manager.support.LanguageManager;
import it.samvise85.bookshelf.model.locale.Language;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

public class InternationalizationResourceResolver extends AbstractResourceResolver {

	@Override
	protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath, List<? extends Resource> locations,
			ResourceResolverChain chain) {
		String path = requestPath;
		Pattern pattern = Pattern.compile(".+\\.(js|html|css)");
		Matcher matcher = pattern.matcher(path);
		if(matcher.matches()) {
			String language = request.getLocale().getLanguage();
			path = createFakePath(requestPath, language);
		}
		Resource resource = chain.resolveResource(request, path, locations);
		return resource;
	}

	@Override
	protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
		String resolvedUrlPath = chain.resolveUrlPath(resourceUrlPath, locations);
		return resolvedUrlPath;
	}

	static String createFakePath(String originalPath, String language) {
		LanguageManager languageManager = ServiceLocator.getInstance().getService(LanguageManager.class);
		Language lang = languageManager.get(language);
		if(lang == null) lang = languageManager.getDefault();
		return originalPath + "_" + lang.getId() + "_" + lang.getVersion();
	}

}
