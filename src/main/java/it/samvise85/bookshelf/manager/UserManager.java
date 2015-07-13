package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.user.User;
import it.samvise85.bookshelf.persist.PersistenceUnit;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

public interface UserManager extends PersistenceUnit<User> {

	User forgotPassword(String id);

	Long countUsers();

	User login(String username);

	User activate(String code);

	User resetPassword(String code, String newPassword);

	User getByUsername(String username, ProjectionClause authenticationProtection);
	
}
