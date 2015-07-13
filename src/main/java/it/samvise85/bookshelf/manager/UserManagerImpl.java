package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.user.User;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.repository.DatabasePersistenceUnit;
import it.samvise85.bookshelf.persist.repository.UserRepository;
import it.samvise85.bookshelf.utils.SHA1Digester;
import it.samvise85.bookshelf.utils.UserUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class UserManagerImpl extends DatabasePersistenceUnit<User> implements UserManager {

	private static final String ACTIVATION_KEY = "activationkey";
	private static final String RESET_KEY = "resetkey";
	@Autowired
	protected UserRepository repository;
	
	public UserManagerImpl() {
		super(User.class);
	}

	/**
	 * Updates the user. Username cannot be changed.
	 */
	@Override
	public User update(User updates) {
		User userToUpdate = get(updates.getId(), UserUtils.NO_PROTECTION);
		
		if(StringUtils.isNotEmpty(updates.getActivationCode()))
			userToUpdate.setActivationCode(updates.getActivationCode());
		if(StringUtils.isNotEmpty(updates.getResetCode()))
			userToUpdate.setResetCode(updates.getResetCode());
		if(updates.getBirthday() != null) {
			userToUpdate.setBirthday(updates.getBirthday());
			userToUpdate.setBirthYear(getBirthYear(updates.getBirthday()));
		}
		if(StringUtils.isNotEmpty(updates.getCountry()))
			userToUpdate.setCountry(updates.getCountry());
		if(updates.getCreation() != null)
			userToUpdate.setCreation(updates.getCreation());
		if(StringUtils.isNotEmpty(updates.getEmail()))
			userToUpdate.setEmail(updates.getEmail());
		if(StringUtils.isNotEmpty(updates.getFirstname()))
			userToUpdate.setFirstname(updates.getFirstname());
		if(StringUtils.isNotEmpty(updates.getLanguage()))
			userToUpdate.setLanguage(updates.getLanguage());
		if(StringUtils.isNotEmpty(updates.getLastname()))
			userToUpdate.setLastname(updates.getLastname());
		if(updates.getAdmin() != null)
			userToUpdate.setAdmin(updates.getAdmin());
		
		//verify the reset code before updating passowrd
		if(StringUtils.isNotEmpty(updates.getPassword()) && StringUtils.isEmpty(userToUpdate.getPassword()) 
				&& StringUtils.isNotEmpty(updates.getResetCode())
				&& updates.getResetCode().equals(userToUpdate.getResetCode()))
			userToUpdate.setPassword(updates.getPassword());
		
		userToUpdate.setLastModification(new Date());
		super.update(userToUpdate);
		
		return get(userToUpdate.getId(), UserUtils.TOTAL_PROTECTION);
	}
	
	@Override
	public User get(Serializable id) {
		return get(id, null);
	}

	@Override
	public User get(Serializable id, ProjectionClause projection) {
		if(projection == null) 
			projection = UserUtils.TOTAL_PROTECTION;
		return super.get(id, projection);
	}

	@Override
	public User getByUsername(String username, ProjectionClause projection) {
		if(projection == null) 
			projection = UserUtils.TOTAL_PROTECTION;
		User user = repository.findOneByUsername(username);
		if(user != null)
			user.setProjection(projection);
		return user;
	}

	@Override
	public List<User> getList(PersistOptions options) {
		if(options == null)
			options = new PersistOptions();
		if(options.getProjection() == null)
			options.setProjection(UserUtils.TOTAL_PROTECTION);
		return super.getList(options);
	}

	@Override
	public User create(User objectToSave) {
		if(objectToSave.getId() == null)
			objectToSave.setId(objectToSave.getUsername().replaceAll("\\W+", "_"));
		
		String data = objectToSave.getUsername() + ":" + new Date().getTime() + ":" + ACTIVATION_KEY;
		objectToSave.setActivationCode(SHA1Digester.digest(data));
		
		super.create(objectToSave);
		return get(objectToSave.getId(), UserUtils.NO_PROTECTION);
	}

	@Override
	public User delete(Serializable id) {
		super.delete(id);
		return null;
	}

	@Override
	public User forgotPassword(String usernameormail) {
		User userToUpdate = repository.findOneByUsername(usernameormail);
		if(userToUpdate == null)
			userToUpdate = repository.findOneByEmail(usernameormail);
		if(userToUpdate == null) return null;
//		User userToUpdate = get(id, UserUtils.NO_PROTECTION);
		
		String data = userToUpdate.getUsername() + ":" + new Date().getTime() + ":" + RESET_KEY;
		userToUpdate.setResetCode(SHA1Digester.digest(data));
		update(userToUpdate);
		return get(userToUpdate.getId(), UserUtils.NO_PROTECTION);
	}

	private static Integer getBirthYear(Date birthday) {
		Calendar cal = Calendar.getInstance();
		int thisYear = cal.get(Calendar.YEAR);

		cal.setTime(birthday);
		int year = cal.get(Calendar.YEAR);
		
		if(year < 1800 || year > thisYear)
			return null;
		return year;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserRepository getRepository() {
		return repository;
	}

	@Override
	public Long countUsers() {
		return repository.count();
	}

	@Override
	public User login(String username) {
		String id = username.replaceAll("\\W+", "_");
		User user = get(id, UserUtils.NO_PROTECTION);
		if(user != null && user.getActivationCode() == null)
			return user.setProjection(UserUtils.PASSWORD_PROTECTION);
		return null;
	}

	@Override
	public User activate(String code) {
		User user = repository.findOneByActivationCode(code);
		if(user == null) return null;
		
		user.setActivationCode(null);
		repository.save(user);
		return user.setProjection(UserUtils.TOTAL_PROTECTION);
	}
	
	@Override
	public User resetPassword(String code, String newPassword) {
		User user = repository.findOneByResetCode(code);
		if(user == null) return null;
		
		user.setResetCode(null);
		user.setPassword(newPassword);
		repository.save(user);
		return user.setProjection(UserUtils.TOTAL_PROTECTION);
	}

}
