package it.samvise85.bookshelf.web.internationalization;

import it.samvise85.bookshelf.context.ServiceLocator;
import it.samvise85.bookshelf.manager.LabelManager;
import it.samvise85.bookshelf.manager.LanguageManager;
import it.samvise85.bookshelf.manager.UserManager;
import it.samvise85.bookshelf.model.Label;
import it.samvise85.bookshelf.model.Language;
import it.samvise85.bookshelf.model.User;
import it.samvise85.bookshelf.utils.MessagesUtil;
import it.samvise85.bookshelf.web.config.SpringSecurityConfig;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.TransformedResource;

public class InternationalizationTransformer {
	private LabelManager labelManager;
	private LanguageManager languageManager;
	private UserManager userManager;
	
	public Resource transform(HttpServletRequest request, Resource resource) throws IOException {
		String username = request.getHeader(SpringSecurityConfig.USERNAME_PARAM_NAME);
		String language = request.getLocale().getLanguage();
		if(username != null) {
			User user = getUserManager().get(username, User.PASSWORD_PROTECTION);
			if(user != null && user.getLanguage() != null)
				language = user.getLanguage();
		}
		
		String fileContent = FileUtils.readFileToString(resource.getFile());
		
		//check markup and substitute labels
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("\\{\\{([^\\}]+)\\}\\}");
		Matcher matcher = pattern.matcher(fileContent);
		while(matcher.find()) {
			String key = matcher.group(1);
			//get label from db
			String replacement = getLabel(language, key);
			matcher.appendReplacement(sb,  replacement);
		}
		matcher.appendTail(sb);
		
		TransformedResource transformedResource = new TransformedResource(resource, sb.toString().getBytes());
		return transformedResource;
	}

	private String getLabel(String language, String key) {
		Label label = getLabelManager().get(key, language);
		String replacement = null;
		if(label != null)
			replacement = label.getLabel();
		else if(isSupported(language)) {
			//create automatically a label entry
			label = new Label();
			label.setId(key + "_" + language);
			label.setLabelKey(key);
			label.setLang(language);
			getLabelManager().create(label);
		}
		if(replacement == null)
			replacement = MessagesUtil.getMessageOrError(key);
		return replacement;
	}
	
	private boolean isSupported(String language) {
		try {
			return getLanguageManager().get(language) != null;
		} catch(Exception e) {return false;}
	}
	
	protected Language getDefaultLanguage() {
		return getLanguageManager().getDefault();
	}

	private LabelManager getLabelManager() {
		if(labelManager == null)
			labelManager = ServiceLocator.getInstance().getService(LabelManager.class);
		return labelManager;
	}
	
	private LanguageManager getLanguageManager() {
		if(languageManager == null)
			languageManager = ServiceLocator.getInstance().getService(LanguageManager.class);
		return languageManager;
	}
	
	private UserManager getUserManager() {
		if(userManager == null)
			userManager = ServiceLocator.getInstance().getService(UserManager.class);
		return userManager;
	}
	
}
