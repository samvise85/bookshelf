package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.book.Section;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;

import org.springframework.stereotype.Service;

@Service
public class SectionManagerImpl extends InMemoryPersistenceUnit<Section> implements SectionManager {

	public SectionManagerImpl() {
		super(Section.class);
	}

}
