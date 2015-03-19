package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.book.Book;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.NoProjectionClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.inmemory.BookRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookManagerImpl implements BookManager {
	@Autowired
	protected BookRepository repository;
	
	@Override
	public Book get(String id) {
		return repository.findOne(id);
	}

	@Override
	public Book get(String id, ProjectionClause projection) {
		return repository.findOne(id);
	}

	@Override
	public List<Book> getList(PersistOptions options) {
//		PageRequest pagination = new PageRequest(options.getPagination().getPage(), options.getPagination().getPageSize());
//		Page<Book> page = repository.findAll(pagination);
		Iterable<Book> page = repository.findAll();
		ArrayList<Book> list = new ArrayList<Book>();
		for(Book b : page)
			list.add(b);
		return list;
	}

	@Override
	public Book delete(String id) {
		// TODO Auto-generated method stub
		return null;
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
		return repository.save(bookToUpdate);
	}

	@Override
	public Book create(Book objectToSave) {
		if(objectToSave.getId() == null)
			objectToSave.setId(objectToSave.getTitle().replaceAll("\\W+", "_"));
		return repository.save(objectToSave);
	}
}
