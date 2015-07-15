package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.UserProfile;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserProfileRepository extends CrudRepository<UserProfile, Long>{

	public List<UserProfile> findByUser(String user);

	public UserProfile findOneByUserAndProfile(String user, String profile);
}
