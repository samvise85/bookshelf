package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.comment.Moderation;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class ModerationManagerImpl extends InMemoryPersistenceUnit<Moderation> implements ModerationManager {

	public ModerationManagerImpl() {
		super(Moderation.class);
	}

	@Override
	public CrudRepository<Moderation, String> getRepository() {
		// TODO Auto-generated method stub
		return null;
	}

}
