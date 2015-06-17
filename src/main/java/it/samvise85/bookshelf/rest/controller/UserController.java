package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.exception.BookshelfSecurityException;
import it.samvise85.bookshelf.manager.UserManager;
import it.samvise85.bookshelf.manager.analytics.RestErrorManager;
import it.samvise85.bookshelf.manager.analytics.RestRequestManager;
import it.samvise85.bookshelf.model.user.BookshelfRole;
import it.samvise85.bookshelf.model.user.User;
import it.samvise85.bookshelf.rest.security.config.SpringSecurityConfig;
import it.samvise85.bookshelf.utils.ControllerUtils;
import it.samvise85.bookshelf.utils.UserUtils;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

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
public class UserController extends AnalyticsAwareController {
	private static final Logger log = Logger.getLogger(UserController.class);
	
	@Autowired
	private UserManager userManager;

	@Autowired
	private RestRequestManager requestManager;

	@Autowired
	private RestErrorManager errorManager;
	
	@RequestMapping("/users")
	@Secured(BookshelfRole.ANYONE)
    public Collection<User> getUserList(HttpServletRequest request) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName);
	}
	
	protected Collection<User> getUserList() {
        return userManager.getList(null);
    }
	
	@RequestMapping("/users/{id}")
    public User getUser(HttpServletRequest request, @PathVariable String id,
    		@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME, required = false) String requestingUser) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, String.class }, new Object[] { id, requestingUser });
	}
	
	protected User getUser(String id, String requestingUser) {
		return userManager.get(id, UserUtils.getFilter(!checkUser(id, requestingUser)));
    }

	@RequestMapping(value="/users", method=RequestMethod.POST)
    public User createUser(HttpServletRequest request, @RequestBody User user,
    		@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME, required = false) String requestingUser) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { User.class, String.class }, new Object[] { user, requestingUser }, user);
	}
	
    protected User createUser(User request, String requestingUser) {
		if(StringUtils.isEmpty(requestingUser)) {
	        return userManager.create(request);
		} else {
			log.warn("A user cannot access this service");
			throw new BookshelfSecurityException("A user cannot access this service");
		}
    }

	@RequestMapping(value="/users/{id}", method=RequestMethod.PUT)
	@Secured(BookshelfRole.ANYONE)
    public User updateUser(HttpServletRequest request, @PathVariable String id, @RequestBody User user,
    		@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME) String requestingUser) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, User.class, String.class }, new Object[] { id, user, requestingUser }, user);
	}
	
	protected User updateUser(String id, User request, String requestingUser) {
		User user = getUser(requestingUser, requestingUser);
		if(checkUser(id, requestingUser) || user.getAdmin()) {
			if(!user.getAdmin()) request.setAdmin(null);
			return userManager.update(request);
		} else {
			throw new BookshelfSecurityException("You cannot edit this user!");
		}
    }

	@RequestMapping(value="/users/{id}", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ANYONE)
    public User deleteUser(HttpServletRequest request, @PathVariable String id,
    		@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME) String requestingUser) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, String.class }, new Object[] { id, requestingUser });
	}
	
    protected User deleteUser(String id, String requestingUser) {
		User user = getUser(requestingUser, requestingUser);
		if(checkUser(id, requestingUser) || user.getAdmin()) {
			return userManager.delete(id);
		} else {
			throw new BookshelfSecurityException("You cannot delete this user!");
		}
    }

	@RequestMapping(value="/users/{id}/forgot", method=RequestMethod.PUT)
    public User resetPassword(HttpServletRequest request, @PathVariable String id) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class }, new Object[] { id });
	}
	
    protected User resetPassword(String id) {
        return userManager.resetPassword(id);
    }
	
	private boolean checkUser(String requestedUser, String username) {
		return StringUtils.isNotEmpty(username) && username.equals(requestedUser);
	}

	@Override
	protected RestRequestManager getRequestManager() {
		return requestManager;
	}

	@Override
	protected RestErrorManager getErrorManager() {
		return errorManager;
	}

	@Override
	protected Logger getLogger() {
		return log;
	}
	
}
