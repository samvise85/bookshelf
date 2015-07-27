package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.exception.BookshelfException;
import it.samvise85.bookshelf.model.Book;
import it.samvise85.bookshelf.model.User;
import it.samvise85.bookshelf.persist.AbstractPersistenceUnit;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.repository.BookRepository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookManagerImpl extends AbstractPersistenceUnit<Book>  implements BookManager {
	@Autowired
	protected BookRepository repository;
	
	@Autowired
	protected UserManager userManager;
	
	public BookManagerImpl() {
		super(Book.class);
	}

	@Override
	public Book update(Book updates) {
		Book bookToUpdate = get(updates.getId(), ProjectionClause.NO_PROJECTION);
		
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
		if(StringUtils.isNotEmpty(updates.getPublishingStatus()))
			bookToUpdate.setPublishingStatus(updates.getPublishingStatus());
		if(updates.getPublishingDate() != null)
			bookToUpdate.setPublishingDate(updates.getPublishingDate());

		bookToUpdate.setLastModification(new Date());	
		return super.update(bookToUpdate);
	}

	@Override
	public Book create(Book objectToSave) {
		if(objectToSave.getId() == null)
			objectToSave.setId(objectToSave.getTitle().replaceAll("\\W+", "_"));
		Book book = get(objectToSave.getId());
		if(book != null)
			throw new BookshelfException("Book " + objectToSave.getId() + " already exists");
		return super.create(objectToSave);
	}

	@SuppressWarnings("unchecked")
	@Override
	public BookRepository getRepository() {
		return repository;
	}

	@Override
	public Book get(Serializable id, ProjectionClause projection) {
		Book book = super.get(id, projection);
		if(book != null) setAuthorname(book);
		return book;
	}

	@Override
	public List<Book> getList(PersistOptions options) {
		List<Book> list = super.getList(options);
		for(Book book : list)
			setAuthorname(book);
		return list;
	}

	private void setAuthorname(Book book) {
		String author = book.getAuthor();
		if(author != null) {
			User user = userManager.get(author);
			if(user == null)
				user = userManager.getByUsername(author, User.TOTAL_PROTECTION);
			if(user != null)
				book.setAuthorname(user.getUsername());
		}
	}
	
}
