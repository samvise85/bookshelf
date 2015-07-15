package it.samvise85.bookshelf.web.security;

import it.samvise85.bookshelf.model.User;
import it.samvise85.bookshelf.model.UserProfile;
import it.samvise85.bookshelf.web.security.BookshelfRole.Role;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

public class BookshelfRoleUtils {
	private static final Logger log = Logger.getLogger(BookshelfRoleUtils.class);

	@Deprecated
	public static List<GrantedAuthority> getGrantedAuthorities(User user) {
		List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
		if(user.getAdmin() != null && user.getAdmin())
			grantedAuths.add(new SimpleGrantedAuthority(BookshelfRole.ADMIN));
		grantedAuths.add(new SimpleGrantedAuthority(BookshelfRole.ANYONE));
		return grantedAuths;
	}

	public static List<GrantedAuthority> getGrantedAuthorities(List<UserProfile> profiles) {
		List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
		grantedAuths.add(new SimpleGrantedAuthority(BookshelfRole.ANYONE));

		if(profiles != null) {
			for(UserProfile profile : profiles) {
				if(!StringUtils.isEmpty(profile.getProfile())) {
					try {
						Role role = Role.valueOf(profile.getProfile());
						if(role != null)
							grantedAuths.add(new SimpleGrantedAuthority(role.getRole()));
					} catch(Exception e) {
						log.warn("Role " + profile.getProfile() + " was requested but doesn't exists!");
					}
				}
			}
		}
		return grantedAuths;
	}
}
