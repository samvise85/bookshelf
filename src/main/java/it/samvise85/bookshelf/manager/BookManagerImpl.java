package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.book.Book;
import it.samvise85.bookshelf.persist.clauses.NoProjectionClause;
import it.samvise85.bookshelf.persist.inmemory.BookRepository;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookManagerImpl extends InMemoryPersistenceUnit<Book>  implements BookManager {
	@Autowired
	protected BookRepository repository;
	
	public BookManagerImpl() {
		super(Book.class);
	}

	@Override
	public Book update(Book updates) {
		Book bookToUpdate = get(updates.getId(), NoProjectionClause.NO_PROJECTION);
		
		if(StringUtils.isNotEmpty(updates.getAuthor()))
			bookToUpdate.setAuthor(updates.getAuthor());
		if(updates.getCreation() != null)
			bookToUpdate.setCreation(updates.getCreation());
		if(StringUtils.isNotEmpty(updates.getGenre()))
			bookToUpdate.setGenre(updates.getGenre());
		if(StringUtils.isNotEmpty(updates.getStream()))
			bookToUpdate.setStream(updates.getStream());
		if(StringUtils.isNotEmpty(updates.getSynopsis()))
			bookToUpdate.setSynopsis(updates.getSynopsis());
		if(StringUtils.isNotEmpty(updates.getTitle()))
			bookToUpdate.setTitle(updates.getTitle());
		if(updates.getYear() != null)
			bookToUpdate.setYear(updates.getYear());

		bookToUpdate.setLastModification(new Date());	
		return super.update(bookToUpdate);
	}

	@Override
	public Book create(Book objectToSave) {
		if(objectToSave.getId() == null)
			objectToSave.setId(objectToSave.getTitle().replaceAll("\\W+", "_"));
		return super.create(objectToSave);
	}

	@SuppressWarnings("unchecked")
	@Override
	public BookRepository getRepository() {
		return repository;
	}
}
