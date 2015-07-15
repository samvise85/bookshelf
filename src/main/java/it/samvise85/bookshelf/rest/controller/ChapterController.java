package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.exception.BookshelfException;
import it.samvise85.bookshelf.manager.ChapterManager;
import it.samvise85.bookshelf.manager.RestErrorManager;
import it.samvise85.bookshelf.manager.RestRequestManager;
import it.samvise85.bookshelf.model.Chapter;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.Order;
import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.PaginationClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.clauses.SelectionClause;
import it.samvise85.bookshelf.persist.clauses.SelectionOperation;
import it.samvise85.bookshelf.utils.BookshelfConstants;
import it.samvise85.bookshelf.utils.ControllerUtils;
import it.samvise85.bookshelf.web.security.BookshelfRole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    public Collection<Chapter> getChapterList(HttpServletRequest request, 
    		@PathVariable String book,
    		@RequestParam(value="page", required=false) Integer page,
    		@RequestParam(value="num", required=false) Integer num) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, Integer.class, Integer.class }, new Object[] { book, page, num });
	}
	
    protected Collection<Chapter> getChapterList(String book, Integer page, Integer num) {
        return chapterManager.getList(new PersistOptions(
        		ProjectionClause.createInclusionClause("id", "position", "number", "title", "book"),
        		Collections.singletonList(new SelectionClause("book", SelectionOperation.EQUALS, book)), 
        		Arrays.asList(new OrderClause[] {new OrderClause("position", Order.ASC), new OrderClause("number", Order.ASC)}),
        		page != null ? new PaginationClause(num != null ? num : BookshelfConstants.Pagination.DEFAULT_PAGE_SIZE, page) : null));
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
    public Chapter getChapterByPosition(HttpServletRequest request, @PathVariable String book, @RequestParam(value="position", required=true) Integer position) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, Integer.class }, new Object[] { book, position });
	}
	
    protected Chapter getChapterByPosition(String book, Integer position) {
		Chapter chapter = chapterManager.getChapterByBookAndPosition(book, position, ProjectionClause.NO_PROJECTION);
        return chapter;
    }
	
	@RequestMapping(value="/books/{book}/chapters", params={"title"})
    public Chapter getChapterByTitle(HttpServletRequest request,
    		@PathVariable String book,
    		@RequestParam(value="title", required=true) String title,
    		@RequestParam(value="pos", required=false) Integer position) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, String.class, Integer.class }, new Object[] { book, title, position });
	}
	
	protected Chapter getChapterByTitle(String book, String title, Integer position) {
		title = decodeParam(title);
		List<SelectionClause> selection = new ArrayList<SelectionClause>();
		selection.add(new SelectionClause("title", SelectionOperation.EQUALS, title));
		selection.add(new SelectionClause("book", SelectionOperation.EQUALS, book));
		if(position != null)
			selection.add(new SelectionClause("position", SelectionOperation.EQUALS, position));
		
		List<Chapter> list = chapterManager.getList(new PersistOptions(ProjectionClause.NO_PROJECTION, selection, null));
		
    	if(list == null || list.isEmpty()) return null;
    	if(list.size() > 1) log.warn("MORE THAN A CHAPTER FOUND BY TITLE " + title + " IN THE BOOK " + book);
    	return list.get(0);
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
