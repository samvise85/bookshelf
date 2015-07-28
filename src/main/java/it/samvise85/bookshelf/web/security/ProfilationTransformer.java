package it.samvise85.bookshelf.web.security;

import it.samvise85.bookshelf.context.ServiceLocator;
import it.samvise85.bookshelf.manager.UserManager;
import it.samvise85.bookshelf.model.User;
import it.samvise85.bookshelf.model.UserProfile;
import it.samvise85.bookshelf.web.config.SpringSecurityConfig;
import it.samvise85.bookshelf.web.security.BookshelfRole.Role;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.TransformedResource;

public class ProfilationTransformer {
	private UserManager userManager;
	
	public Resource transform(HttpServletRequest request, TransformedResource resource) throws IOException {
		String username = request.getHeader(SpringSecurityConfig.USERNAME_PARAM_NAME);
		List<Role> profiles = new ArrayList<Role>();
		if(username != null) {
			User user = getUserManager().getByUsername(username, User.TOTAL_PROTECTION);
			if(user != null) {
				List<UserProfile> list = getUserManager().getProfiles(user.getId());
				for(UserProfile pr : list) {
					Role role = Role.valueOf(pr.getProfile());
					if(role != null) profiles.add(role);
				}
			}
		}
		if(profiles.isEmpty())
			profiles.add(Role.UNKNOWN);
		
		String fileContent = new String(resource.getByteArray());
		
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("\\[\\[PROFILE([^\\]]+)\\]\\]([^\\[]*)\\[\\[\\/PROFILE\\]\\]", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(fileContent);
		while(matcher.find()) {
			String[] includedRoles = null;
			String[] excludedRoles = null;
			String attributes = matcher.group(1);
			Pattern includedPattern = Pattern.compile("in=\"([^\"]+)");
			Matcher includedMatcher = includedPattern.matcher(attributes);
			if(includedMatcher.find()) {
				String includedString = includedMatcher.group(1);
				includedRoles = includedString.split(",");
			}
			Pattern excludedPattern = Pattern.compile("out=\"([^\"]+)");
			Matcher excludedMatcher = excludedPattern.matcher(attributes);
			if(excludedMatcher.find()) {
				String excludedString = excludedMatcher.group(1);
				excludedRoles = excludedString.split(",");
			}
			
			String content = matcher.group(2);
			boolean include = false;
			if(includedRoles != null)
				for(String role : includedRoles)
					if(profiles.contains(Role.valueOf(role)))
						include = true;
			if(excludedRoles != null)
				for(String role : excludedRoles)
					if(profiles.contains(Role.valueOf(role)))
						include = false;
			
			if(!include) content = "";
			matcher.appendReplacement(sb, content);
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
