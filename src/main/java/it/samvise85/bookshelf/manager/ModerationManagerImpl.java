package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.comment.Moderation;
import it.samvise85.bookshelf.persist.repository.DatabasePersistenceUnit;
import it.samvise85.bookshelf.persist.repository.ModerationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModerationManagerImpl extends DatabasePersistenceUnit<Moderation> implements ModerationManager {
	@Autowired
	private ModerationRepository repository;
	
	public ModerationManagerImpl() {
		super(Moderation.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ModerationRepository getRepository() {
		return repository;
	}

}
