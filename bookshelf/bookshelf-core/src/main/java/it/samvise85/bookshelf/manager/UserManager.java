package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.user.User;
import it.samvise85.bookshelf.persist.PersistenceUnit;

public interface UserManager extends PersistenceUnit<User> {

	User resetPassword(String id);
	
}
