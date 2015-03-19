package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.book.Section;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class SectionManagerImpl extends InMemoryPersistenceUnit<Section> implements SectionManager {

	public SectionManagerImpl() {
		super(Section.class);
	}

	@Override
	public CrudRepository<Section, String> getRepository() {
		// TODO Auto-generated method stub
		return null;
	}

}
