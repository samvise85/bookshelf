package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.support.LabelManager;
import it.samvise85.bookshelf.manager.support.LanguageManager;
import it.samvise85.bookshelf.model.locale.Label;
import it.samvise85.bookshelf.model.locale.Language;
import it.samvise85.bookshelf.model.user.BookshelfRole;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.NoProjectionClause;
import it.samvise85.bookshelf.persist.clauses.Order;
import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.PaginationClause;
import it.samvise85.bookshelf.persist.clauses.SelectionClause;
import it.samvise85.bookshelf.persist.selection.Equals;
import it.samvise85.bookshelf.utils.ControllerConstants;
import it.samvise85.bookshelf.utils.ControllerUtils;

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
public class LabelController {
	private static final Logger log = Logger.getLogger(LabelController.class);
	
	@Autowired
	private LabelManager labelManager;

	@Autowired
	private LanguageManager languageManager;

	@RequestMapping(value="/labels")
	@Secured(BookshelfRole.ADMIN)
	public Collection<Label> getLabelList(@RequestParam(value="language", required=false) String language,
			@RequestParam(value="page", required=false) Integer page,
    		@RequestParam(value="num", required=false) Integer num) {
		String methodName = ControllerUtils.getMethodName();
		log.info(methodName);
		return labelManager.getList(new PersistOptions(
        		NoProjectionClause.NO_PROJECTION,
        		Collections.singletonList(new SelectionClause("lang", Equals.getInstance(), language)),
        		Collections.singletonList(new OrderClause("key", Order.ASC)),
        		page != null ? new PaginationClause(num != null ? num : ControllerConstants.Pagination.DEFAULT_PAGE_SIZE, page) : null));
	}

	@RequestMapping(value="/labels", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
	public Boolean clearLabels() {
		labelManager.deleteAll();
		return true;
	}

	@RequestMapping(value="/labels/{id}")
	@Secured(BookshelfRole.ADMIN)
    public Label getLabel(@PathVariable String id,
    		@RequestParam(value="language", required=false) String language,
    		@RequestParam(value="default", required=false) Boolean bundleLabel) {
		log.info(ControllerUtils.getMethodName() + ": id = " + id + "; language = " + language + "; bundleLabel = " + bundleLabel);
		
		if(bundleLabel) return labelManager.getDefault(id); //id is the key
		if(language != null) labelManager.get(id, language); //id is the key
		return labelManager.get(id); //id is the id
    }

	@RequestMapping(value="/labels/{id}", method=RequestMethod.PUT)
	@Secured(BookshelfRole.ADMIN)
    public Label updateLabel(@PathVariable String id, @RequestBody Label label) {
		log.info(ControllerUtils.getMethodName() + ": id = " + id);
		labelManager.update(label);
		return null;
    }

	@RequestMapping(value="/languages")
	public List<Language> getLanguageList(@RequestParam(value="page", required=false) Integer page) {
		String methodName = ControllerUtils.getMethodName();
		log.info(methodName);
		return languageManager.getList(new PersistOptions(
        		NoProjectionClause.NO_PROJECTION, null, null,
        		page != null ? new PaginationClause(ControllerConstants.Pagination.DEFAULT_PAGE_SIZE, page) : null));
	}

	@RequestMapping(value="/languages/{id}", method=RequestMethod.PUT)
	@Secured(BookshelfRole.ADMIN)
    public Language updateLanguage(@PathVariable String id, @RequestBody Language language) {
		log.info(ControllerUtils.getMethodName() + ": id = " + id);
		return languageManager.update(language);
    }
}
