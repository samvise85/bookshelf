package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.support.LabelManager;
import it.samvise85.bookshelf.model.locale.Label;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.NoProjectionClause;
import it.samvise85.bookshelf.persist.clauses.PaginationClause;
import it.samvise85.bookshelf.utils.ControllerConstants;
import it.samvise85.bookshelf.utils.ControllerUtils;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LabelController {
	private static final Logger log = Logger.getLogger(LabelController.class);
	
	@Autowired
	private LabelManager labelManager;

	@RequestMapping(value="/labels")
//	@Secured(BookshelfRole.ADMIN)
	public Collection<Label> getLabelList(@RequestParam(value="page", required=false) Integer page) {
		String methodName = ControllerUtils.getMethodName();
		log.info(methodName);
		return labelManager.getList(new PersistOptions(
        		NoProjectionClause.NO_PROJECTION, null, null,
        		page != null ? new PaginationClause(ControllerConstants.Pagination.DEFAULT_PAGE_SIZE, page) : null));
	}

	@RequestMapping(value="/labels/{id}")
//	@Secured(BookshelfRole.ADMIN)
    public Label getLabel(@PathVariable String id) {
		log.info(ControllerUtils.getMethodName() + ": id = " + id);
		return labelManager.get(Long.parseLong(id));
    }

	@RequestMapping(value="/labels/{id}", method=RequestMethod.PUT)
//	@Secured(BookshelfRole.ADMIN)
    public Label updateLabel(@PathVariable String id, @RequestBody Label label) {
		log.info(ControllerUtils.getMethodName() + ": id = " + id);
		labelManager.update(label);
		return null;
    }

}
