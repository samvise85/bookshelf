package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.UserManager;
import it.samvise85.bookshelf.model.user.BookshelfRole;
import it.samvise85.bookshelf.model.user.User;
import it.samvise85.bookshelf.rest.security.config.SpringSecurityConfig;
import it.samvise85.bookshelf.utils.UserUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
	@Autowired
	private UserManager userManager;
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@Secured(BookshelfRole.ANYONE)
	public User login(@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME) String username) {
		return userManager.get(username, UserUtils.PASSWORD_PROTECTION);
	}
}
