package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.RestErrorManager;
import it.samvise85.bookshelf.manager.RestRequestManager;
import it.samvise85.bookshelf.manager.UserManager;
import it.samvise85.bookshelf.model.User;
import it.samvise85.bookshelf.utils.ControllerUtils;
import it.samvise85.bookshelf.web.config.SpringSecurityConfig;
import it.samvise85.bookshelf.web.security.BookshelfRole;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController extends AnalyticsAwareController {
	private static final Logger log = Logger.getLogger(LoginController.class);
	
	@Autowired
	private UserManager userManager;

	@Autowired
	private RestRequestManager requestManager;

	@Autowired
	private RestErrorManager errorManager;
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@Secured(BookshelfRole.ANYONE)
	public User login(HttpServletRequest request, @RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME) String username) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class }, new Object[] { username });
	}
	
	protected User login(String username) {
		return userManager.login(username);
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
