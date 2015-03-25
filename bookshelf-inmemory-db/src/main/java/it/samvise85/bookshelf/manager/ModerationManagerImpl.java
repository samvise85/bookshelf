package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.comment.Moderation;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;
import it.samvise85.bookshelf.persist.inmemory.ModerationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModerationManagerImpl extends InMemoryPersistenceUnit<Moderation> implements ModerationManager {
	@Autowired
	private ModerationRepository repository;
	
	public ModerationManagerImpl() {
		super(Moderation.class);
	}

	@Override
	public ModerationRepository getRepository() {
		return repository;
	}

}
