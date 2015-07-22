package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.SettingManager;
import it.samvise85.bookshelf.manager.UserManager;
import it.samvise85.bookshelf.model.User;
import it.samvise85.bookshelf.model.UserProfile;
import it.samvise85.bookshelf.model.dto.ResponseDto;
import it.samvise85.bookshelf.utils.AppVersion;
import it.samvise85.bookshelf.utils.SHA1Digester;
import it.samvise85.bookshelf.web.security.BookshelfRole.Role;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigureController extends AbstractController {
	private static final Logger log = Logger.getLogger(ConfigureController.class);
	
	@Autowired
	private UserManager userManager;
	@Autowired
	private SettingManager settingManager;

	@Override
	protected Logger getLogger() {
		return log;
	}

	@RequestMapping(value = "/configure", method = RequestMethod.PUT)
	public ResponseDto configure(HttpServletRequest request) {
		String methodName = getMethodName();
		return executeMethod(request, methodName);
	}

	protected String configure() {
		if(userManager.countUsers() == 0) {
			try {
				User admin = new User();
				admin.setId("admin");
				admin.setUsername("admin");
				admin.setPassword(SHA1Digester.digest("prova"));
				admin.setFirstname("");
				admin.setLastname("Administrator");
				admin.setLanguage("en");
				admin.setAdmin(true);
				userManager.create(admin);

				UserProfile up = new UserProfile();
				up.setUser(admin.getId());
				up.setProfile(Role.ANYONE.name());
				userManager.createProfile(up);
				up = new UserProfile();
				up.setUser(admin.getId());
				up.setProfile(Role.ADMIN.name());
				userManager.createProfile(up);
				
				User user = new User();
				user.setId("user");
				user.setUsername("user");
				user.setPassword(SHA1Digester.digest("password"));
				user.setFirstname("Normal");
				user.setLastname("User");
				user.setLanguage("en");
				user.setAdmin(false);
				userManager.create(user);
				
				up = new UserProfile();
				up.setUser(user.getId());
				up.setProfile(Role.ANYONE.name());
				userManager.createProfile(up);
				
				return "Configured! Try acces with admin/prova";
			} catch(Exception e) {
				log.error(e.getMessage(), e);
				return "Error configuring see log!";	
			}
		} else {
			return "You cannot configure the application!";
		}
	}

	@RequestMapping(value = "/updateVersion", method = RequestMethod.PUT)
	public ResponseDto update(HttpServletRequest request) {
		String methodName = getMethodName();
		return executeMethod(request, methodName);
	}

	protected String update() {
		String appVersion = settingManager.getAppVersion();
		AppVersion version = AppVersion.findByVersionCode(appVersion);
		
		if(version == null) {
			updateFromA();
		} else {
			switch(version) {
			case AGRAJAG:
				updateFromA();
			case BLART_VERSENWALT_III:
				updateFromB();
			default:
				break;
			}
		}
		return "ok";
	}

	private void updateFromA() {
		//set profiles
		List<User> list = userManager.getList(null);
		for(User user : list) {
			UserProfile up = new UserProfile();
			up.setUser(user.getId());
			up.setProfile(Role.ANYONE.name());
			userManager.createProfile(up);
			if(user.getAdmin() != null && user.getAdmin()) {
				up = new UserProfile();
				up.setUser(user.getId());
				up.setProfile(Role.ADMIN.name());
				userManager.createProfile(up);
			}
		}
		settingManager.setAppVersion(AppVersion.BLART_VERSENWALT_III.getVersionCode());
	}

	private void updateFromB() {
		//for future versions
		
	}
}
