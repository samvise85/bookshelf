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
		Pattern pattern = Pattern.compile("(\\[\\[([^\\]]+)\\]\\](.*)\\[\\[\\/\\2\\]\\])", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(fileContent);
		while(matcher.find()) {
			String role = matcher.group(2);
			String content = matcher.group(3);
			if(profiles.contains(Role.valueOf(role)))
				matcher.appendReplacement(sb, content);
			else
				matcher.appendReplacement(sb, "");
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
