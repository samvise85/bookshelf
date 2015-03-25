package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.ChapterManager;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

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
public class ChapterController implements ControllerCommons {
	private static final Logger log = Logger.getLogger(ChapterController.class);
	
	@Autowired
	private ChapterManager chapterManager;

	@RequestMapping(value="/books/{book}/chapters")
    public Collection<Chapter> getChapterList(@PathVariable String book, @RequestParam(value="page", required=false) Integer page) {
		log.info("getChapterList PAGINATED!");
        return chapterManager.getList(new PersistOptions(
        		new SimpleProjectionClause("id", "position", "number", "title", "book"),
        		Collections.singletonList(new SelectionClause("book", Equals.getInstance(), book)), 
        		Arrays.asList(new OrderClause[] {new OrderClause("position", Order.ASC), new OrderClause("number", Order.ASC)}),
        		page != null ? new PaginationClause(Pagination.DEFAULT_PAGE_SIZE, page) : null));
    }
	
	@RequestMapping("/books/{book}/chapters/{id}")
    public Chapter getChapter(@PathVariable String id) {
		log.info("getChapter: id = " + id);
        return chapterManager.get(id);
    }
	
	@RequestMapping(value="/books/{book}/chapters", params={"position"})
    public Chapter getChapterByPosition(@PathVariable String book, @RequestParam(value="position", required=false) Integer position) {
		log.info("getChapter: book = " + book + "; position = " + position);
		Chapter chapter = chapterManager.getChapterByBookAndPosition(book, position, NoProjectionClause.NO_PROJECTION);
        return chapter;
    }

	@RequestMapping(value="/books/{book}/chapters", method=RequestMethod.POST)
	@Secured(BookshelfRole.ADMIN)
    public Chapter createChapter(@RequestBody Chapter request) {
		log.info("createChapter: request = " + request);
		if(request.getBook() != null) {
	        return chapterManager.create(request);
		} else {
			log.error("The chapter " + request.getTitle() + " has no book!");
		}
		return null;
    }

	@RequestMapping(value="/books/{book}/chapters/{id}", method=RequestMethod.PUT)
	@Secured(BookshelfRole.ADMIN)
    public Chapter updateChapter(@PathVariable String id, @RequestBody Chapter request) {
		log.info("updateChapter: id = " + id + "; request = " + request);
		request.setLastModification(new Date());
        return chapterManager.update(request);
    }

	@RequestMapping(value="/books/{book}/chapters/{id}", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public Chapter deleteChapter(@PathVariable String id) {
		log.info("deleteChapter: id = " + id);
        return chapterManager.delete(id);
    }
}
