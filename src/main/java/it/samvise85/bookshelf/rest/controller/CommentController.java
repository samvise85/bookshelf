package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.exception.BookshelfException;
import it.samvise85.bookshelf.exception.BookshelfSecurityException;
import it.samvise85.bookshelf.manager.CommentManager;
import it.samvise85.bookshelf.manager.analytics.RestErrorManager;
import it.samvise85.bookshelf.manager.analytics.RestRequestManager;
import it.samvise85.bookshelf.model.comment.Comment;
import it.samvise85.bookshelf.model.user.BookshelfRole;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.NoProjectionClause;
import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.PaginationClause;
import it.samvise85.bookshelf.persist.clauses.SelectionClause;
import it.samvise85.bookshelf.persist.selection.Equals;
import it.samvise85.bookshelf.rest.security.config.SpringSecurityConfig;
import it.samvise85.bookshelf.utils.ControllerConstants;
import it.samvise85.bookshelf.utils.ControllerUtils;

import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController extends AnalyticsAwareController {
	private static final Logger log = Logger.getLogger(CommentController.class);
	
	@Autowired
	private CommentManager commentManager;

	@Autowired
	private RestRequestManager requestManager;

	@Autowired
	private RestErrorManager errorManager;

	@RequestMapping(value="/streams/{stream}/comments")
    public Collection<Comment> getCommentList(HttpServletRequest request, @PathVariable String stream, @RequestParam(value="page", required=false) Integer page) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, Integer.class }, new Object[] { stream, page });
	}
	
    protected Collection<Comment> getCommentList(String stream, Integer page) {
        return commentManager.getList(new PersistOptions(
        		NoProjectionClause.NO_PROJECTION,
        		Collections.singletonList(new SelectionClause("parentStream", Equals.getInstance(), stream)),
        		Collections.singletonList(OrderClause.DESC("creation")),
        		page != null ? new PaginationClause(ControllerConstants.Pagination.DEFAULT_PAGE_SIZE, page) : null));
    }
	
	@RequestMapping("/streams/{stream}/comments/{id}")
    public Comment getComment(HttpServletRequest request, @PathVariable String stream, @PathVariable String id) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, String.class }, new Object[] { stream, id });
	}
	
    protected Comment getComment(String stream, String id) {
        return commentManager.get(id);
    }
	
	@RequestMapping(value="/streams/{stream}/comments", method=RequestMethod.POST)
	@Secured(BookshelfRole.ANYONE)
    public Comment createComment(HttpServletRequest request, @PathVariable String stream, @RequestBody Comment comment) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, Comment.class }, new Object[] { stream, comment }, comment);
	}
	
    protected Comment createComment(String stream, Comment request) {
		if(request.getStream() != null)
	        return commentManager.create(request);
		throw new BookshelfException("The comment has no stream!");
    }

	@RequestMapping(value="/streams/{stream}/comments/{id}", method=RequestMethod.PUT)
	@Secured(BookshelfRole.ANYONE)
    public Comment updateComment(HttpServletRequest request, @PathVariable String stream, @PathVariable String id, @RequestBody Comment comment, 
    		@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME) String username) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, String.class, Comment.class, String.class }, new Object[] { stream, id, comment, username }, comment);
	}
	
    protected Comment updateComment(String stream, String id, Comment request, String username) {
		Comment comment = getComment(stream, id);
		if(comment.getUser().equals(username))
			return commentManager.update(request);
		throw new BookshelfSecurityException("You cannot modify this object");
    }

	@RequestMapping(value="/streams/{stream}/comments/{id}", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ANYONE)
    public Comment deleteComment(HttpServletRequest request, @PathVariable String stream, @PathVariable String id,
    		@RequestHeader(value=SpringSecurityConfig.USERNAME_PARAM_NAME) String username) {
		String methodName = ControllerUtils.getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, String.class, String.class }, new Object[] { stream, id, username });
	}
	
    protected Comment deleteComment(String stream, String id, String username) {
		Comment comment = getComment(stream, id);
		if(comment.getUser().equals(username))
			return commentManager.delete(id);
		throw new BookshelfSecurityException("You cannot delete this object");
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
