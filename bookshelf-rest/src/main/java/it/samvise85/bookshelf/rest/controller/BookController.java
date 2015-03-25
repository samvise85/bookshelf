package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.BookManager;
import it.samvise85.bookshelf.model.book.Book;
import it.samvise85.bookshelf.model.user.BookshelfRole;

import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {
	private static final Logger log = Logger.getLogger(BookController.class);
	
	@Autowired
	private BookManager bookManager;
	
	@RequestMapping("/books")
    public Collection<Book> getBookList() {
		log.info("getBookList");
        return bookManager.getList(null);
    }
	
	@RequestMapping("/books/{id}")
    public Book getBook(@PathVariable String id) {
		log.info("getBook: id = " + id);
        return bookManager.get(id);
    }

	@RequestMapping(value="/books", method=RequestMethod.POST)
	@Secured(BookshelfRole.ADMIN)
    public Book createBook(@RequestBody Book request) {
		log.info("createBook: request = " + request);
		request.setCreation(new Date());
        return bookManager.create(request);
    }

	@RequestMapping(value="/books/{id}", method=RequestMethod.PUT)
	@Secured(BookshelfRole.ADMIN)
    public Book updateBook(@PathVariable String id, @RequestBody Book request) {
		log.info("updateBook: id = " + id + "; request = " + request);
		request.setLastModification(new Date());
        return bookManager.update(request);
    }

	@RequestMapping(value="/books/{id}", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public Book deleteBook(@PathVariable String id) {
		log.info("deleteBook: id = " + id);
        return bookManager.delete(id);
    }
}
