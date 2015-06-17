package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.exception.BookshelfException;
import it.samvise85.bookshelf.manager.ChapterManager;
import it.samvise85.bookshelf.manager.analytics.RestErrorManager;
import it.samvise85.bookshelf.manager.analytics.RestRequestManager;
import it.samvise85.bookshelf.model.book.Chapter;
import it.samvise85.bookshelf.model.user.BookshelfRole;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.NoProjectionClause;
import it.samvise85.bookshelf.persist.clauses.Order;
import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.PaginationClause;
import it.samvise85.bookshelf.persist.clauses.SelectionClause;
import it.samvise85.bookshelf.persist.clauses.SimpleProjectionClause;
import it.samvise85.bookshelf.persist.selection.Equals;
import it.samvise85.bookshelf.utils.ControllerConstants;
import it.samvise85.bookshelf.utils.ControllerUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChapterController extends AnalyticsAwareController {
	private static final Logger log = Logger.getLogger(ChapterController.class);
	
	@Autowired
	private ChapterManager chapterManager;

	@Autowired
	private RestRequestManager requestManager;

	@Autowired
	private RestErrorManager errorManager;
	
	@RequestMapping(value="/books/{book}/chapters")
    public Collection<Chapter> getChapterList(HttpServletRequest request, @PathVariable String book, @RequestParam(value="page", required=false) Integer page) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, Integer.class }, new Object[] { book, page });
	}
	
    protected Collection<Chapter> getChapterList(String book, Integer page) {
        return chapterManager.getList(new PersistOptions(
        		new SimpleProjectionClause("id", "position", "number", "title", "book"),
        		Collections.singletonList(new SelectionClause("book", Equals.getInstance(), book)), 
        		Arrays.asList(new OrderClause[] {new OrderClause("position", Order.ASC), new OrderClause("number", Order.ASC)}),
        		page != null ? new PaginationClause(ControllerConstants.Pagination.DEFAULT_PAGE_SIZE, page) : null));
    }
	
	@RequestMapping("/books/{book}/chapters/{id}")
    public Chapter getChapter(HttpServletRequest request, @PathVariable String id) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class }, new Object[] { id });
	}
	
    protected Chapter getChapter(String id) {
        return chapterManager.get(id);
    }
	
	@RequestMapping(value="/books/{book}/chapters", params={"position"})
    public Chapter getChapterByPosition(HttpServletRequest request, @PathVariable String book, @RequestParam(value="position", required=false) Integer position) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, Integer.class }, new Object[] { book, position });
	}
	
    protected Chapter getChapterByPosition(String book, Integer position) {
		Chapter chapter = chapterManager.getChapterByBookAndPosition(book, position, NoProjectionClause.NO_PROJECTION);
        return chapter;
    }

	@RequestMapping(value="/books/{book}/chapters", method=RequestMethod.POST)
	@Secured({BookshelfRole.ADMIN, BookshelfRole.AUTHOR})
    public Chapter createChapter(HttpServletRequest request, @RequestBody Chapter chapter) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { Chapter.class }, new Object[] { chapter }, chapter);
	}
	
    protected Chapter createChapter(Chapter request) {
		if(request.getBook() != null)
	        return chapterManager.create(request);
		throw new BookshelfException("The chapter " + request.getTitle() + " has no book!");
    }

	@RequestMapping(value="/books/{book}/chapters/{id}", method=RequestMethod.PUT)
	@Secured(BookshelfRole.ADMIN)
    public Chapter updateChapter(HttpServletRequest request, @PathVariable String id, @RequestBody Chapter chapter) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, Chapter.class }, new Object[] { id, chapter }, chapter);
	}
	
    protected Chapter updateChapter(String id, Chapter request) {
		request.setLastModification(new Date());
        return chapterManager.update(request);
    }

	@RequestMapping(value="/books/{book}/chapters/{id}", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public Chapter deleteChapter(HttpServletRequest request, @PathVariable String id) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class }, new Object[] { id });
	}
	
    protected Chapter deleteChapter(@PathVariable String id) {
        return chapterManager.delete(id);
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
