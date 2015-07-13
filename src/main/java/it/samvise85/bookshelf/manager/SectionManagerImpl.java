package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.book.Section;
import it.samvise85.bookshelf.persist.repository.DatabasePersistenceUnit;
import it.samvise85.bookshelf.persist.repository.SectionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SectionManagerImpl extends DatabasePersistenceUnit<Section> implements SectionManager {
	@Autowired
	private SectionRepository repository;
	
	public SectionManagerImpl() {
		super(Section.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SectionRepository getRepository() {
		return repository;
	}

}
