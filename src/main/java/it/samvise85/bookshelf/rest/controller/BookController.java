package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.BookManager;
import it.samvise85.bookshelf.manager.RestErrorManager;
import it.samvise85.bookshelf.manager.RestRequestManager;
import it.samvise85.bookshelf.model.Book;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.utils.ControllerUtils;
import it.samvise85.bookshelf.web.security.BookshelfRole;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController extends AnalyticsAwareController {
	private static final Logger log = Logger.getLogger(BookController.class);
	
	@Autowired
	private BookManager bookManager;

	@Autowired
	private RestRequestManager requestManager;

	@Autowired
	private RestErrorManager errorManager;

	@RequestMapping("/books")
    public Collection<Book> getBookList(HttpServletRequest request) {
		String methodName = ControllerUtils.getMethodName();
		
		return executeMethod(request, methodName, new Class<?>[] { Map.class }, new Object[] { request.getParameterMap() });
    }

	protected Collection<Book> getBookList(Map<String, String[]> queryParams) {
		if(queryParams == null || queryParams.isEmpty())
			return bookManager.getList(null);
		return bookManager.getList(new PersistOptions(ProjectionClause.NO_PROJECTION, 
				getSelectionFromParameterMap(queryParams), 
				null));
	}

	@RequestMapping("/books/{id}")
    public Book getBook(HttpServletRequest request, @PathVariable String id) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class }, new Object[] { id });
	}
	
	protected Book getBook(String id) {
        return bookManager.get(id);
    }

	@RequestMapping(value="/books", method=RequestMethod.POST)
	@Secured({BookshelfRole.ADMIN, BookshelfRole.AUTHOR})
	public Book createBook(HttpServletRequest request, @RequestBody Book book) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { Book.class }, new Object[] { book }, book);
	}
	
	protected Book createBook(Book request) {
		request.setCreation(new Date());
        return bookManager.create(request);
    }

	@RequestMapping(value="/books/{id}", method=RequestMethod.PUT)
	@Secured({BookshelfRole.ADMIN, BookshelfRole.AUTHOR})
    public Book updateBook(HttpServletRequest request, @PathVariable String id, @RequestBody Book book) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, Book.class }, new Object[] { id, book }, book);
	}
	
	protected Book updateBook(String id, Book request) {
		request.setLastModification(new Date());
        return bookManager.update(request);
    }

	@RequestMapping(value="/books/{id}", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public Book deleteBook(HttpServletRequest request, @PathVariable String id) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class }, new Object[] { id });
	}
	
	protected Book deleteBook(String id) {
        return bookManager.delete(id);
    }

	@Override
	protected RestRequestManager getRequestManager() {
		return requestManager;
	}

	@Override
	protected RestErrorManager getErrorManager() {
		return errorManager;
	}
	
	@Override
	protected Logger getLogger() {
		return log;
	}
}
