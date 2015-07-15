package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.SettingManager;
import it.samvise85.bookshelf.model.Setting;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.Order;
import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.PaginationClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.utils.BookshelfConstants;
import it.samvise85.bookshelf.utils.ControllerUtils;
import it.samvise85.bookshelf.web.security.BookshelfRole;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
public class SettingsController extends AbstractController {
	private static final Logger log = Logger.getLogger(SettingsController.class);
	
	@Autowired
	private SettingManager settingManager;

	@RequestMapping(value="/settings")
	@Secured(BookshelfRole.ADMIN)
	public Collection<Setting> getSettingsList(HttpServletRequest request,
			@RequestParam(value="page", required=false) Integer page,
    		@RequestParam(value="num", required=false) Integer num) {
		log.info(ControllerUtils.getMethodName());
		Map<String, String[]> queryParams = new HashMap<String, String[]>(request.getParameterMap());
		queryParams.remove("page");
		queryParams.remove("num");
		
		return settingManager.getList(new PersistOptions(
        		ProjectionClause.NO_PROJECTION,
        		getSelectionFromParameterMap(queryParams),
        		Collections.singletonList(new OrderClause("id", Order.ASC)),
        		page != null ? new PaginationClause(num != null ? num : BookshelfConstants.Pagination.DEFAULT_PAGE_SIZE, page) : null));
	}

	@RequestMapping(value="/settings/{id}", method=RequestMethod.PUT)
	@Secured(BookshelfRole.ADMIN)
    public Setting updateLabel(@PathVariable String id, @RequestBody Setting setting) {
		log.info(ControllerUtils.getMethodName() + ": id = " + id);
		return settingManager.saveSetting(setting.getId(), setting.getValue());
    }

	@Override
	protected Logger getLogger() {
		return log;
	}
}
