package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.UserManager;
import it.samvise85.bookshelf.model.user.User;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigureController {
	private static final Logger log = Logger.getLogger(ConfigureController.class);
	
	@Autowired
	private UserManager userManager;
	
	@RequestMapping(value = "/configure", method = RequestMethod.PUT)
	public String configure() {
		if(userManager.countUsers() == 0) {
			try {
				User admin = new User();
				admin.setId("admin");
				admin.setUsername("admin");
				admin.setPassword("prova");
				admin.setFirstname("");
				admin.setLastname("Administrator");
				admin.setLanguage("en");
				admin.setAdmin(true);
				userManager.create(admin);

				User user = new User();
				user.setId("user");
				user.setUsername("user");
				user.setPassword("password");
				user.setFirstname("Normal");
				user.setLastname("User");
				user.setLanguage("en");
				user.setAdmin(false);
				userManager.create(user);
				
				return "Configured! Try acces with admin/prova";
			} catch(Exception e) {
				log.error(e.getMessage(), e);
				return "Error configuring see log!";	
			}
		} else {
			return "You cannot configure the application!";
		}
	}
}
