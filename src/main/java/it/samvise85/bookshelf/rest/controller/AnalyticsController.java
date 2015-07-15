package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.RestErrorManager;
import it.samvise85.bookshelf.manager.RestRequestManager;
import it.samvise85.bookshelf.manager.RouteManager;
import it.samvise85.bookshelf.model.RestError;
import it.samvise85.bookshelf.model.RestRequest;
import it.samvise85.bookshelf.model.Route;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.PaginationClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.utils.BookshelfConstants;
import it.samvise85.bookshelf.utils.ControllerUtils;
import it.samvise85.bookshelf.web.security.BookshelfRole;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
public class AnalyticsController {
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
	public Collection<RestRequest> getRequestList(@RequestParam(value="page", required=false) Integer page) {
		String methodName = ControllerUtils.getMethodName();
		log.info(methodName);
		return requestManager.getList(new PersistOptions(
        		ProjectionClause.NO_PROJECTION, null, null,
        		page != null ? new PaginationClause(BookshelfConstants.Pagination.DEFAULT_PAGE_SIZE, page) : null));
	}

	@RequestMapping(value="/analytics/requests/{id}")
	@Secured(BookshelfRole.ADMIN)
    public RestRequest getRequest(@PathVariable String id) {
		log.info(ControllerUtils.getMethodName() + ": id = " + id);
		return requestManager.get(Long.parseLong(id));
    }

	@RequestMapping(value="/analytics/requests/{id}", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public RestRequest deleteRequest(@PathVariable String id) {
		log.info(ControllerUtils.getMethodName() + ": id = " + id);
		requestManager.delete(Long.parseLong(id));
		return null;
    }

	@RequestMapping(value="/analytics/requests", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public int clearRequests() {
		log.info(ControllerUtils.getMethodName());
		List<RestRequest> list = requestManager.getList(null);
		for(RestRequest req : list)
			requestManager.delete(req.getId());
		return list.size();
    }

	@RequestMapping("/analytics/errors")
	@Secured(BookshelfRole.ADMIN)
	public Collection<RestError> getErrorList(@RequestParam(value="request", required=false) Long request) {
		String methodName = ControllerUtils.getMethodName();
		log.info(methodName);
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
    public RestError deleteError(@PathVariable Long id) {
		log.info(ControllerUtils.getMethodName() + ": id = " + id);
        errorManager.delete(id);
        return null;
    }

	@RequestMapping(value="/analytics/errors", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public int clearErrors() {
		log.info(ControllerUtils.getMethodName());
		List<RestRequest> list = requestManager.getList(null);
		for(RestRequest req : list)
			requestManager.delete(req.getId());
		return list.size();
    }

	@RequestMapping("/analytics/routes")
	@Secured(BookshelfRole.ADMIN)
	public Collection<Route> getRouteList() {
		String methodName = ControllerUtils.getMethodName();
		log.info(methodName);
		return routeManager.getList(null);
	}

	@RequestMapping(value="/analytics/routes", method=RequestMethod.POST)
    public void createRoute(@RequestBody Route request) {
		log.info(ControllerUtils.getMethodName() + ": request = " + request);
//		request.setCreation(new Date());
        routeManager.create(request);
    }

	@RequestMapping(value="/analytics/routes/{id}", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public Route deleteRoute(@PathVariable Long id) {
		log.info(ControllerUtils.getMethodName() + ": id = " + id);
        routeManager.delete(id);
        return null;
    }

	@RequestMapping(value="/analytics/routes", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public int clearRoutes() {
		log.info(ControllerUtils.getMethodName());
		List<RestRequest> list = requestManager.getList(null);
		for(RestRequest req : list)
			requestManager.delete(req.getId());
		return list.size();
    }

	@RequestMapping(value="/analytics", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
    public int clearAnalytics() {
		log.info(ControllerUtils.getMethodName());
		int res = clearRequests();
		res += clearErrors();
		res += clearRoutes();
		return res;
    }
}
