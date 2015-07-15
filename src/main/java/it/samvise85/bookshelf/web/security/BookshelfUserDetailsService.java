package it.samvise85.bookshelf.web.security;

import it.samvise85.bookshelf.exception.PersistException;
import it.samvise85.bookshelf.manager.UserManager;
import it.samvise85.bookshelf.model.User;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookshelfUserDetailsService implements UserDetailsService, InitializingBean {

	@Autowired
	private UserManager userManager;

	public void afterPropertiesSet() throws Exception {
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		username = username.toLowerCase();
		User user = null;
		try {
			user = userManager.getByUsername(username, User.AUTHENTICATION_PROTECTION);
		} catch(PersistException e) {
			return null;
		}
		if (user == null)
			return null;

		List<GrantedAuthority> auths = BookshelfRoleUtils.getGrantedAuthorities(user);
		
		return new org.springframework.security.core.userdetails.User(username, user.getPassword(), true, true, true, true, auths);
	}

}