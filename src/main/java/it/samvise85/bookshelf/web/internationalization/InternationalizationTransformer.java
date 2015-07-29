package it.samvise85.bookshelf.web.internationalization;

import it.samvise85.bookshelf.context.ServiceLocator;
import it.samvise85.bookshelf.manager.UserManager;
import it.samvise85.bookshelf.model.User;
import it.samvise85.bookshelf.utils.MessagesUtil;
import it.samvise85.bookshelf.web.config.BookshelfTransformer;
import it.samvise85.bookshelf.web.config.SpringSecurityConfig;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.TransformedResource;

public class InternationalizationTransformer extends BookshelfTransformer {
	private UserManager userManager;
	
	public TransformedResource transform(HttpServletRequest request, Resource resource) throws IOException {
		String username = request.getHeader(SpringSecurityConfig.USERNAME_PARAM_NAME);
		String language = request.getLocale().getLanguage();
		if(username != null) {
			User user = getUserManager().get(username, User.PASSWORD_PROTECTION);
			if(user != null && user.getLanguage() != null)
				language = user.getLanguage();
		}
		
		String fileContent = getResourceContent(resource);
		
		//check markup and substitute labels
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("\\{\\{([^\\}]+)\\}\\}");
		Matcher matcher = pattern.matcher(fileContent);
		while(matcher.find()) {
			String key = matcher.group(1);
			//get label from db
			String replacement = MessagesUtil.getLabel(language, key);
			matcher.appendReplacement(sb,  replacement);
		}
		matcher.appendTail(sb);
		
		TransformedResource transformedResource = new TransformedResource(resource, sb.toString().getBytes());
		return transformedResource;
	}
	
	private UserManager getUserManager() {
		if(userManager == null)
			userManager = ServiceLocator.getInstance().getService(UserManager.class);
		return userManager;
	}
	
}
