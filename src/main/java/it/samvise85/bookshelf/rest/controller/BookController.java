package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.BookManager;
import it.samvise85.bookshelf.manager.RestErrorManager;
import it.samvise85.bookshelf.manager.RestRequestManager;
import it.samvise85.bookshelf.model.Book;
import it.samvise85.bookshelf.model.dto.PublishingStatus;
import it.samvise85.bookshelf.model.dto.ResponseDto;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.clauses.SelectionClause;
import it.samvise85.bookshelf.persist.clauses.SelectionOperation;
import it.samvise85.bookshelf.web.config.SpringSecurityConfig;
import it.samvise85.bookshelf.web.security.BookshelfRole;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    public ResponseDto getBookList(HttpServletRequest request,
    		@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME, required = false) String user) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { Map.class, String.class }, new Object[] { request.getParameterMap(), user });
    }

	protected Collection<Book> getBookList(Map<String, String[]> queryParams, String requestingUser) {
		if(queryParams == null || queryParams.isEmpty())
			return bookManager.getList(null);
		List<SelectionClause> list = getSelectionFromParameterMap(queryParams);
		list.add(new SelectionClause("publishingStatus", SelectionOperation.NOT_EQUALS, PublishingStatus.DRAFT.name()));
		return bookManager.getList(new PersistOptions(ProjectionClause.NO_PROJECTION, list, OrderClause.list(OrderClause.DESC("publishingDate"))));
	}

	@RequestMapping("/books/{id}")
    public ResponseDto getBook(HttpServletRequest request, @PathVariable String id) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class }, new Object[] { id });
	}
	
	protected Book getBook(String id) {
        return bookManager.get(id);
    }

	@RequestMapping(value="/books", method=RequestMethod.POST)
	@Secured({BookshelfRole.ADMIN, BookshelfRole.AUTHOR})
	public ResponseDto createBook(HttpServletRequest request, @RequestBody Book book) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { Book.class }, new Object[] { book }, book);
	}
	
	protected Book createBook(Book request) {
		request.setCreation(new Date());
        return bookManager.create(request);
    }

	@RequestMapping(value="/books/{id}", method=RequestMethod.PUT)
	@Secured({BookshelfRole.ADMIN, BookshelfRole.AUTHOR})
    public ResponseDto updateBook(HttpServletRequest request, @PathVariable String id, @RequestBody Book book) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, Book.class }, new Object[] { id, book }, book);
	}
	
	protected Book updateBook(String id, Book request) {
		request.setLastModification(new Date());
        return bookManager.update(request);
    }

	@RequestMapping(value="/books/{id}", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public ResponseDto deleteBook(HttpServletRequest request, @PathVariable String id) {
		String methodName = getMethodName();
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
