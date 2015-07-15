package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.Moderation;
import it.samvise85.bookshelf.persist.AbstractPersistenceUnit;
import it.samvise85.bookshelf.persist.repository.ModerationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModerationManagerImpl extends AbstractPersistenceUnit<Moderation> implements ModerationManager {
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
