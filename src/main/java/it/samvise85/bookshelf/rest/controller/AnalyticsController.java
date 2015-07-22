package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.RestErrorManager;
import it.samvise85.bookshelf.manager.RestRequestManager;
import it.samvise85.bookshelf.manager.RouteManager;
import it.samvise85.bookshelf.model.RestError;
import it.samvise85.bookshelf.model.RestRequest;
import it.samvise85.bookshelf.model.Route;
import it.samvise85.bookshelf.model.dto.ResponseDto;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.PaginationClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.utils.BookshelfConstants;
import it.samvise85.bookshelf.web.security.BookshelfRole;

import java.util.Collection;
import java.util.Collections;
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
public class AnalyticsController extends AbstractController {
	private static final Logger log = Logger.getLogger(AnalyticsController.class);
	
	private static final ProjectionClause ERROR_STACK_TRACE_PROJECTION = ProjectionClause.createExclusionClause("stackTrace");

	@Autowired
	private RestRequestManager requestManager;

	@Autowired
	private RestErrorManager errorManager;

	@Autowired
	private RouteManager routeManager;

	@RequestMapping(value="/analytics/requests")
	@Secured(BookshelfRole.ADMIN)
	public ResponseDto getRequestList(HttpServletRequest request, @RequestParam(value="page", required=false) Integer page) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { Integer.class }, new Object[] { page });
	}
	
	protected List<RestRequest> getRequestList(Integer page) {
		return requestManager.getList(new PersistOptions(
        		ProjectionClause.NO_PROJECTION, null, null,
        		page != null ? new PaginationClause(BookshelfConstants.Pagination.DEFAULT_PAGE_SIZE, page) : null));
	}

	@RequestMapping(value="/analytics/requests/{id}")
	@Secured(BookshelfRole.ADMIN)
    public ResponseDto getRequest(HttpServletRequest request, @PathVariable String id) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class }, new Object[] { id });
    }

	protected RestRequest getRequest(String id) {
		return requestManager.get(Long.parseLong(id));
    }

	@RequestMapping(value="/analytics/requests/{id}", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public ResponseDto deleteRequest(HttpServletRequest request, @PathVariable String id) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class }, new Object[] { id });
    }
	
	protected RestRequest deleteRequest(String id) {
		requestManager.delete(Long.parseLong(id));
		return null;
    }

	@RequestMapping(value="/analytics/requests", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public ResponseDto clearRequests(HttpServletRequest request) {
		String methodName = getMethodName();
		return executeMethod(request, methodName);
    }
	
	protected int clearRequests() {
		List<RestRequest> list = requestManager.getList(null);
		for(RestRequest req : list)
			requestManager.delete(req.getId());
		return list.size();
    }

	@RequestMapping("/analytics/errors")
	@Secured(BookshelfRole.ADMIN)
	public ResponseDto getErrorList(HttpServletRequest request, @RequestParam(value="request", required=false) Long requestId) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { Long.class }, new Object[] { requestId });
	}
	
	protected Collection<RestError> getErrorList(Long request) {
		if(request == null)
			return errorManager.getList(new PersistOptions(ERROR_STACK_TRACE_PROJECTION, null, null));
		else {
			RestError requestError = errorManager.getRequestError(request);
			if(requestError == null) return null;
			return Collections.singletonList(requestError);
		}
	}

	@RequestMapping(value="/analytics/errors/{id}", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public ResponseDto deleteError(HttpServletRequest request, @PathVariable Long id) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { Long.class }, new Object[] { id });
    }
	
	protected RestError deleteError(Long id) {
        errorManager.delete(id);
        return null;
    }

	@RequestMapping(value="/analytics/errors", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public ResponseDto clearErrors(HttpServletRequest request) {
		String methodName = getMethodName();
		return executeMethod(request, methodName);
    }
	
	protected int clearErrors() {
		List<RestRequest> list = requestManager.getList(null);
		for(RestRequest req : list)
			requestManager.delete(req.getId());
		return list.size();
    }

	@RequestMapping("/analytics/routes")
	@Secured(BookshelfRole.ADMIN)
	public ResponseDto getRouteList(HttpServletRequest request) {
		String methodName = getMethodName();
		return executeMethod(request, methodName);
	}

	protected Collection<Route> getRouteList() {
		return routeManager.getList(null);
	}

	@RequestMapping(value="/analytics/routes", method=RequestMethod.POST)
    public ResponseDto createRoute(HttpServletRequest request, @RequestBody Route route) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { Route.class }, new Object[] { route });
    }

	protected void createRoute(Route request) {
//		request.setCreation(new Date());
        routeManager.create(request);
    }

	@RequestMapping(value="/analytics/routes/{id}", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public ResponseDto deleteRoute(HttpServletRequest request, @PathVariable Long id) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { Long.class }, new Object[] { id });
    }

	protected Route deleteRoute(Long id) {
        routeManager.delete(id);
        return null;
    }

	@RequestMapping(value="/analytics/routes", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public ResponseDto clearRoutes(HttpServletRequest request) {
		String methodName = getMethodName();
		return executeMethod(request, methodName);
    }

	protected int clearRoutes() {
		List<RestRequest> list = requestManager.getList(null);
		for(RestRequest req : list)
			requestManager.delete(req.getId());
		return list.size();
    }

	@RequestMapping(value="/analytics", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public ResponseDto clearAnalytics(HttpServletRequest request) {
		String methodName = getMethodName();
		return executeMethod(request, methodName);
    }
	
	protected int clearAnalytics() {
		int res = clearRequests();
		res += clearErrors();
		res += clearRoutes();
		return res;
    }

	@Override
	protected Logger getLogger() {
		return log;
	}
}
