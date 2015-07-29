package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.exception.BookshelfSecurityException;
import it.samvise85.bookshelf.manager.RestErrorManager;
import it.samvise85.bookshelf.manager.RestRequestManager;
import it.samvise85.bookshelf.manager.UserManager;
import it.samvise85.bookshelf.model.User;
import it.samvise85.bookshelf.model.dto.ResponseDto;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.web.config.SpringSecurityConfig;
import it.samvise85.bookshelf.web.security.BookshelfRole;

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
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseDto getUserList(HttpServletRequest request) {
		String methodName = getMethodName();
		return executeMethod(request, methodName);
	}
	
	protected Collection<User> getUserList() {
        return userManager.getList(null);
    }
	
	@RequestMapping("/users/{id}")
    public ResponseDto getUser(HttpServletRequest request, @PathVariable String id,
    		@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME, required = false) String requestingUser) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, String.class }, new Object[] { id, requestingUser });
	}
	
	protected User getUser(String id, String requestingUser) {
		return userManager.get(id, getFilter(!checkUser(id, requestingUser)));
    }
	
	@RequestMapping(value="/users/{id}", params={"username"})
    public ResponseDto getUserByUsername(HttpServletRequest request, @RequestParam(value="username", required=true) String username) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class }, new Object[] { username });
	}
	
	protected User getUserByUsername(String username) {
		return userManager.getByUsername(username, User.TOTAL_PROTECTION);
    }

	@RequestMapping(value="/users", method=RequestMethod.POST)
    public ResponseDto createUser(HttpServletRequest request, @RequestBody User user,
    		@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME, required = false) String requestingUser) {
		String methodName = getMethodName();
		
		return executeMethod(request, methodName, new Class<?>[] { User.class, String.class }, new Object[] { user, requestingUser }, user);
	}
	
    protected User createUser(User request, String requestingUser) {
		if(StringUtils.isEmpty(requestingUser)) {
			User newuser = userManager.create(request);
			mailSender.sendSubscriptionMail(newuser);
	        return userManager.get(newuser.getId());
		} else {
			log.warn("A user cannot access this service");
			throw new BookshelfSecurityException("A user cannot access this service");
		}
    }

	@RequestMapping(value="/users/{id}", method=RequestMethod.PUT)
	@Secured(BookshelfRole.ANYONE)
    public ResponseDto updateUser(HttpServletRequest request, @PathVariable String id, @RequestBody User user,
    		@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME) String requestingUser) {
		String methodName = getMethodName();
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
    public ResponseDto deleteUser(HttpServletRequest request, @PathVariable String id,
    		@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME) String requestingUser) {
		String methodName = getMethodName();
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
    public ResponseDto resetPassword(HttpServletRequest request, @PathVariable String id) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class }, new Object[] { id });
	}
	
    protected User resetPassword(String id) {
        return userManager.forgotPassword(id);
    }

	@RequestMapping(value="/activationCode/{code}", method=RequestMethod.PUT)
    public ResponseDto activate(HttpServletRequest request, @PathVariable String code) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class }, new Object[] { code });
	}
	
    protected User activate(String code) {
        return userManager.activate(code);
    }

	@RequestMapping(value="/forgot", method=RequestMethod.PUT)
    public ResponseDto forgot(HttpServletRequest request, @RequestBody User user) {
		String methodName = getMethodName();
		String usernameormail = user.getUsername();
		return executeMethod(request, methodName, new Class<?>[] { String.class }, new Object[] { usernameormail });
	}
	
	public User forgot(String usernameormail) {
		User user = userManager.forgotPassword(usernameormail);
		mailSender.sendResetMail(user);
        return userManager.get(user.getId());
	}

	@RequestMapping(value="/resetCode/{code}", method=RequestMethod.PUT)
    public ResponseDto reset(HttpServletRequest request, @PathVariable String code, @RequestBody User user) {
		String methodName = getMethodName();
		String password = user.getPassword();
		return executeMethod(request, methodName, new Class<?>[] { String.class, String.class }, new Object[] { code, password });
	}
	
    protected User reset(String code, String password) {
        return userManager.resetPassword(code, password);
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
	
	private boolean checkUser(String requestedUser, String username) {
		return StringUtils.isNotEmpty(username) && username.equals(requestedUser);
	}

	private static ProjectionClause getFilter(boolean concealInfo) {
		if(concealInfo) {
			//TODO metti solo informazioni che l'utente sceglie di condividere
			return User.TOTAL_PROTECTION;
		}
		return User.PASSWORD_PROTECTION;
	}
}
