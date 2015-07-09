package it.samvise85.bookshelf.persist.database;

import it.samvise85.bookshelf.model.user.User;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, String> {
 
    public User findByUsername(String username);

	public User findOneByActivationCode(String code);
}