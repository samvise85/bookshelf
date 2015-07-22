package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.LabelManager;
import it.samvise85.bookshelf.manager.LanguageManager;
import it.samvise85.bookshelf.model.Label;
import it.samvise85.bookshelf.model.Language;
import it.samvise85.bookshelf.model.dto.ResponseDto;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.Order;
import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.PaginationClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.clauses.SelectionClause;
import it.samvise85.bookshelf.persist.clauses.SelectionOperation;
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
public class LabelController extends AbstractController {
	private static final Logger log = Logger.getLogger(LabelController.class);
	
	@Autowired
	private LabelManager labelManager;

	@Autowired
	private LanguageManager languageManager;

	@RequestMapping(value="/labels")
	@Secured(BookshelfRole.ADMIN)
	public ResponseDto getLabelList(HttpServletRequest request, @RequestParam(value="language", required=false) String language,
			@RequestParam(value="page", required=false) Integer page,
    		@RequestParam(value="num", required=false) Integer num) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, Integer.class, Integer.class }, new Object[] { language, page, num });
	}
	
	protected Collection<Label> getLabelList(String language, Integer page, Integer num) {
		return labelManager.getList(new PersistOptions(
        		ProjectionClause.NO_PROJECTION,
        		Collections.singletonList(new SelectionClause("lang", SelectionOperation.EQUALS, language)),
        		Collections.singletonList(new OrderClause("key", Order.ASC)),
        		page != null ? new PaginationClause(num != null ? num : BookshelfConstants.Pagination.DEFAULT_PAGE_SIZE, page) : null));
	}

	@RequestMapping(value="/labels", method=RequestMethod.DELETE)
	@Secured(BookshelfRole.ADMIN)
	public ResponseDto clearLabels(HttpServletRequest request) {
		String methodName = getMethodName();
		return executeMethod(request, methodName);
	}
	
	protected Boolean clearLabels() {
		labelManager.deleteAll();
		return true;
	}

	@RequestMapping(value="/labels/{id}")
	@Secured(BookshelfRole.ADMIN)
    public ResponseDto getLabel(HttpServletRequest request, @PathVariable String id,
    		@RequestParam(value="language", required=false) String language,
    		@RequestParam(value="default", required=false) Boolean bundleLabel) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, String.class, Boolean.class }, new Object[] { id, language, bundleLabel });
	}
	
    protected Label getLabel(String id, String language, Boolean bundleLabel) {
		if(bundleLabel) return labelManager.getDefault(id); //id is the key
		if(language != null) labelManager.get(id, language); //id is the key
		return labelManager.get(id); //id is the id
    }

	@RequestMapping(value="/labels/{id}", method=RequestMethod.PUT)
	@Secured(BookshelfRole.ADMIN)
    public ResponseDto updateLabel(HttpServletRequest request, @PathVariable String id, @RequestBody Label label) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, Label.class }, new Object[] { id, label });
	}
	
    protected Label updateLabel(@PathVariable String id, @RequestBody Label label) {
		labelManager.update(label);
		return null;
    }

	@RequestMapping(value="/languages")
	public ResponseDto getLanguageList(HttpServletRequest request, @RequestParam(value="page", required=false) Integer page) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { Integer.class }, new Object[] { page });
	}
	
	protected List<Language> getLanguageList(Integer page) {
		return languageManager.getList(new PersistOptions(
        		ProjectionClause.NO_PROJECTION, null, null,
        		page != null ? new PaginationClause(BookshelfConstants.Pagination.DEFAULT_PAGE_SIZE, page) : null));
	}

	@RequestMapping(value="/languages/{id}", method=RequestMethod.PUT)
	@Secured(BookshelfRole.ADMIN)
    public ResponseDto updateLanguage(HttpServletRequest request, @PathVariable String id, @RequestBody Language language) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class, Language.class }, new Object[] { id, language });
	}
	
    protected Language updateLanguage(String id, Language language) {
		return languageManager.update(language);
    }

	@Override
	protected Logger getLogger() {
		return log;
	}
}
