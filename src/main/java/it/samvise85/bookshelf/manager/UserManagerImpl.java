package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.User;
import it.samvise85.bookshelf.model.UserProfile;
import it.samvise85.bookshelf.persist.AbstractPersistenceUnit;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.clauses.SelectionClause;
import it.samvise85.bookshelf.persist.clauses.SelectionOperation;
import it.samvise85.bookshelf.persist.repository.UserProfileRepository;
import it.samvise85.bookshelf.persist.repository.UserRepository;
import it.samvise85.bookshelf.utils.SHA1Digester;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class UserManagerImpl extends AbstractPersistenceUnit<User> implements UserManager {

	private static final String ACTIVATION_KEY = "activationkey";
	private static final String RESET_KEY = "resetkey";
	@Autowired
	protected UserRepository repository;
	@Autowired
	protected UserProfileRepository profileRepository;
	
	public UserManagerImpl() {
		super(User.class);
	}

	/**
	 * Updates the user. Username cannot be changed.
	 */
	@Override
	public User update(User updates) {
		User userToUpdate = get(updates.getId(), User.NO_PROTECTION);
		
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
		
		return get(userToUpdate.getId(), User.TOTAL_PROTECTION);
	}
	
	@Override
	public User get(Serializable id) {
		return get(id, null);
	}

	@Override
	public User get(Serializable id, ProjectionClause projection) {
		if(projection == null) 
			projection = User.TOTAL_PROTECTION;
		return getOne(new PersistOptions(projection, Collections.singletonList(new SelectionClause("id", SelectionOperation.EQUALS, id)), null));
	}

	@Override
	public User getByUsername(String username, ProjectionClause projection) {
		if(projection == null) 
			projection = User.TOTAL_PROTECTION;
		return repository.searchOne(new PersistOptions(projection, Collections.singletonList(new SelectionClause("username", SelectionOperation.EQUALS, username)), null));
	}

	@Override
	public List<User> getList(PersistOptions options) {
		if(options == null)
			options = new PersistOptions();
		if(options.getProjection() == null)
			options.setProjection(User.TOTAL_PROTECTION);
		return repository.search(options);
	}

	@Override
	public User create(User objectToSave) {
		if(objectToSave.getId() == null)
			objectToSave.setId(objectToSave.getUsername().replaceAll("\\W+", "_"));
		
		String data = objectToSave.getUsername() + ":" + new Date().getTime() + ":" + ACTIVATION_KEY;
		objectToSave.setActivationCode(SHA1Digester.digest(data));
		
		super.create(objectToSave);
		return get(objectToSave.getId(), User.NO_PROTECTION);
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
		
		String data = userToUpdate.getUsername() + ":" + new Date().getTime() + ":" + RESET_KEY;
		userToUpdate.setResetCode(SHA1Digester.digest(data));
		update(userToUpdate);
		return get(userToUpdate.getId(), User.NO_PROTECTION);
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
		User user = getByUsername(username, User.NO_PROTECTION);
		if(user != null && user.getActivationCode() == null)
			return user.setProjection(User.PASSWORD_PROTECTION);
		return null;
	}

	@Override
	public User activate(String code) {
		User user = repository.findOneByActivationCode(code);
		if(user == null) return null;
		
		user.setActivationCode(null);
		repository.save(user);
		return user.setProjection(User.TOTAL_PROTECTION);
	}
	
	@Override
	public User resetPassword(String code, String newPassword) {
		User user = repository.findOneByResetCode(code);
		if(user == null) return null;
		
		user.setResetCode(null);
		user.setPassword(newPassword);
		repository.save(user);
		return user.setProjection(User.TOTAL_PROTECTION);
	}

	@Override
	public List<UserProfile> getProfiles(String id) {
		return profileRepository.findByUser(id);
	}

	@Override
	public UserProfile createProfile(UserProfile up) {
		UserProfile profile = profileRepository.findOneByUserAndProfile(up.getUser(), up.getProfile());
		if(profile != null) return profile;
		return profileRepository.save(up);
	}

	@Override
	public User getOne(PersistOptions persistOptions) {
		return repository.searchOne(persistOptions);
	}

}
