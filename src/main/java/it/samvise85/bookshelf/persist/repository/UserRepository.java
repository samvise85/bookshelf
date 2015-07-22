package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.User;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends SearchRepository<User, String> {
 
    public User findByUsername(String username);

	public User findOneByActivationCode(String code);

	public User findOneByResetCode(String code);

	public User findOneByUsername(String id);
	
	public User findOneByEmail(String id);
}