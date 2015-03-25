package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.user.User;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.file.FilePersistenceUnit;
import it.samvise85.bookshelf.utils.UserUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public final class UserManagerImpl extends FilePersistenceUnit<User> implements UserManager {

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
	public User get(String id) {
		return get(id, null);
	}

	@Override
	public User get(String id, ProjectionClause projection) {
		if(projection == null) 
			projection = UserUtils.TOTAL_PROTECTION;
		return super.get(id, projection);
	}

//	@Override
//	public List<User> getList(List<SelectionClause> selection, List<OrderClause> order) {
//		return getList(null, selection, order);
//	}
//
//	@Override
//	public List<User> getList(ProjectionClause projection, List<SelectionClause> selection, List<OrderClause> order) {
//		if(projection == null)
//			projection = UserUtils.TOTAL_PROTECTION;
//		return super.getList(projection, selection, order);
//	}

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
			objectToSave.setId(objectToSave.getUsername());
		super.create(objectToSave);
		return get(objectToSave.getId());
	}

	@Override
	public User delete(String id) {
		super.delete(id);
		return null;
	}

	@Override
	public User resetPassword(String id) {
		User userToUpdate = get(id, UserUtils.NO_PROTECTION);
		UserUtils.resetPassword(userToUpdate);
		return update(userToUpdate);
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
}
