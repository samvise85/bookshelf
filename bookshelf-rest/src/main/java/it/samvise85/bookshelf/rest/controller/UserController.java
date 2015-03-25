package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.UserManager;
import it.samvise85.bookshelf.model.user.BookshelfRole;
import it.samvise85.bookshelf.model.user.User;
import it.samvise85.bookshelf.rest.security.config.SpringSecurityConfig;
import it.samvise85.bookshelf.utils.UserUtils;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	private static final Logger log = Logger.getLogger(UserController.class);
	
	@Autowired
	private UserManager userManager;
	
	@RequestMapping("/users")
    public Collection<User> getUserList() {
		log.info("getUserList");
        return userManager.getList(null);
    }
	
	@RequestMapping("/users/{id}")
    public User getUser(@PathVariable String id,
    		@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME) String username) {
		log.info("getUser: id = " + id);
		return userManager.get(id, UserUtils.getFilter(!checkUser(id, username)));
    }

	@RequestMapping(value="/users", method=RequestMethod.POST)
    public User createUser(@RequestBody User request) {
		log.info("createUser: request = " + request);
        return userManager.create(request);
    }

	@RequestMapping(value="/users/{id}", method=RequestMethod.PUT)
	@Secured(BookshelfRole.ANYONE)
    public User updateUser(@PathVariable String id, @RequestBody User request,
    		@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME) String username) {
		log.info("updateUser: id = " + id + "; request = " + request);
		if(checkUser(id, username)) {
			return userManager.update(request);
		}
		//TODO throw unauthorized
		return null;
    }

	@RequestMapping(value="/users/{id}", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ANYONE)
    public User deleteUser(@PathVariable String id,
    		@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME) String username) {
		log.info("deleteUser: id = " + id);
		if(checkUser(id, username)) {
			return userManager.delete(id);
		}
		//TODO throw unauthorized
		return null;
    }

	@RequestMapping(value="/users/{id}/forgot", method=RequestMethod.PUT)
    public User resetPassword(@PathVariable String id) {
		log.info("deleteUser: id = " + id);
        return userManager.resetPassword(id);
    }
	
	private boolean checkUser(String requestedUser, String username) {
		return StringUtils.isNotEmpty(username) && username.equals(requestedUser);
	}
	
}
