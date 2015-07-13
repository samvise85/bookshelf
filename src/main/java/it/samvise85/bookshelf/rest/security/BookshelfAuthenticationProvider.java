package it.samvise85.bookshelf.rest.security;

import it.samvise85.bookshelf.manager.UserManager;
import it.samvise85.bookshelf.model.user.User;
import it.samvise85.bookshelf.persist.exception.PersistException;
import it.samvise85.bookshelf.utils.UserUtils;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;

@Controller
public class BookshelfAuthenticationProvider implements AuthenticationProvider {
	private static final Logger log = Logger.getLogger(BookshelfAuthenticationProvider.class);
	
	@Autowired
	private UserManager userManager;
	
	@Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        try {
        	User user = userManager.getByUsername(name, UserUtils.AUTHENTICATION_PROTECTION);
        	if(user.getPassword() != null && user.getPassword().equals(password)) {
        		List<GrantedAuthority> grantedAuths = BookshelfSecurityUtils.getGrantedAuthorities(user);
        		return new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
        	}
        } catch(PersistException e) {
        	log.warn("User " + name + " doesn't exists.");
        }
        return null;
    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
