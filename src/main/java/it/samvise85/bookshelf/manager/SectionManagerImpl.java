package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.book.Section;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;
import it.samvise85.bookshelf.persist.inmemory.SectionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SectionManagerImpl extends InMemoryPersistenceUnit<Section> implements SectionManager {
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
