package it.samvise85.bookshelf.web.internationalization;

import it.samvise85.bookshelf.context.ServiceLocator;
import it.samvise85.bookshelf.manager.LanguageManager;
import it.samvise85.bookshelf.manager.UserManager;
import it.samvise85.bookshelf.model.Language;
import it.samvise85.bookshelf.model.User;
import it.samvise85.bookshelf.web.config.SpringSecurityConfig;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

public class InternationalizationResourceResolver extends AbstractResourceResolver {

	private UserManager userManager;

	@Override
	protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath, List<? extends Resource> locations,
			ResourceResolverChain chain) {
		String path = requestPath;
		Pattern pattern = Pattern.compile(".+\\.(js|html|css)");
		Matcher matcher = pattern.matcher(path);
		if(matcher.matches()) {
			String language = request.getLocale().getLanguage();
			String username = request.getHeader(SpringSecurityConfig.USERNAME_PARAM_NAME);
			if(username != null) {
				User user = getUserManager().get(username, User.PASSWORD_PROTECTION);
				if(user != null && user.getLanguage() != null)
					language = user.getLanguage();
			}
			
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

	private UserManager getUserManager() {
		if(this.userManager == null)
			this.userManager = ServiceLocator.getInstance().getService(UserManager.class);
		return this.userManager;
	}
	
}
