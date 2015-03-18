package it.samvise85.bookshelf.rest.security;

import it.samvise85.bookshelf.model.user.BookshelfRole;
import it.samvise85.bookshelf.model.user.User;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class BookshelfSecurityUtils {

	public static List<GrantedAuthority> getGrantedAuthorities(User user) {
		List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
		if(user.getAdmin() != null && user.getAdmin())
			grantedAuths.add(new SimpleGrantedAuthority(BookshelfRole.ADMIN));
		grantedAuths.add(new SimpleGrantedAuthority(BookshelfRole.ANYONE));
		return grantedAuths;
	}

}
