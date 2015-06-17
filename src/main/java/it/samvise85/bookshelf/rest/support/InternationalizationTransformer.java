package it.samvise85.bookshelf.rest.support;

import it.samvise85.bookshelf.context.ServiceLocator;
import it.samvise85.bookshelf.manager.UserManager;
import it.samvise85.bookshelf.manager.support.LabelManager;
import it.samvise85.bookshelf.model.locale.Label;
import it.samvise85.bookshelf.model.user.User;
import it.samvise85.bookshelf.rest.security.config.SpringSecurityConfig;
import it.samvise85.bookshelf.utils.UserUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.TransformedResource;

public class InternationalizationTransformer {
	private LabelManager labelManager;
	private UserManager userManager;
	
	static Set<Locale> supportedLanguages = new HashSet<Locale>();
	static {
		supportedLanguages.add(Locale.ITALIAN);
		supportedLanguages.add(Locale.ENGLISH);
	}

	public Resource transform(HttpServletRequest request, Resource resource) throws IOException {
		String username = request.getHeader(SpringSecurityConfig.USERNAME_PARAM_NAME);
		String language = request.getLocale().getLanguage();
		if(username != null) {
			User user = getUserManager().get(username, UserUtils.PASSWORD_PROTECTION);
			if(user.getLanguage() != null)
				language = user.getLanguage();
		}
		
		String fileContent = FileUtils.readFileToString(resource.getFile());
		
		//check markup and substitute labels
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("\\{\\{(.+)\\}\\}");
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
		else if(supportedLanguages.contains(Locale.forLanguageTag(language))) {
			//create automatically a label entry
			label = new Label();
			label.setId(key + "_" + language);
			label.setKey(key);
			label.setLang(language);
			getLabelManager().create(label);
		}
		if(replacement == null)
			replacement = "??"+key+"??";
		return replacement;
	}
	
	private LabelManager getLabelManager() {
		if(labelManager == null)
			labelManager = ServiceLocator.getInstance().getService(LabelManager.class);
		return labelManager;
	}
	
	private UserManager getUserManager() {
		if(userManager == null)
			userManager = ServiceLocator.getInstance().getService(UserManager.class);
		return userManager;
	}
}
