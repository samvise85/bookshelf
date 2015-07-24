package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.exception.BookshelfException;
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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        		Collections.singletonList(new OrderClause("labelKey", Order.ASC)),
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

	@RequestMapping(value="/languages/messages_{id}.properties", method=RequestMethod.GET)
	@Secured(BookshelfRole.ADMIN)
    public ResponseDto getLanguageProperties(HttpServletRequest request, @PathVariable String id) {
		String methodName = getMethodName();
		return executeMethod(request, methodName, new Class<?>[] { String.class }, new Object[] { id });
	}
	
    protected String getLanguageProperties(String id) {
    	List<Label> list = labelManager.getList(new PersistOptions(ProjectionClause.NO_PROJECTION,
    			SelectionClause.list(new SelectionClause("lang", SelectionOperation.EQUALS, id)), 
    			OrderClause.list(OrderClause.ASC("labelKey"))));
    	StringBuilder sb = new StringBuilder();
    	boolean first = true;
    	for(Label label : list) {
    		if(first) first = false;
    		else sb.append("\n");
    		sb.append(label.getLabelKey());
    		sb.append("=");
    		if(label.getLabel() != null)
    			sb.append(label.getLabel());
    	}
    	return sb.toString();
    }

	@Controller
	public static class UploadController extends AbstractController {
		@Autowired
		private LabelManager labelManager;

		@Autowired
		private LanguageManager languageManager;
		
		@RequestMapping(value="/languages/{id}/properties", method=RequestMethod.POST)
		@Secured(BookshelfRole.ADMIN)
	    public @ResponseBody ResponseDto updateLanguageLabels(HttpServletRequest request, @PathVariable String id, @RequestParam("file") MultipartFile file) {
			String methodName = getMethodName();
			return executeMethod(request, methodName, new Class<?>[] { String.class, MultipartFile.class }, new Object[] { id, file });
		}
	
		protected boolean updateLanguageLabels(String id, MultipartFile file) throws IOException {
			Properties prop = new Properties();
			try {
				prop.load(file.getInputStream());
			} catch(IOException e) {
				throw new BookshelfException(e.getMessage(), e);
			}

			if(prop.size() > 0) {
				Enumeration<Object> keys = prop.keys();
				while(keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					String value = prop.getProperty(key);
					Label label = labelManager.get(key, id);
					if(!StringUtils.isEmpty(value)) {
						if(label == null) {
							label = new Label();
							label.setId(key + "_" + id);
							label.setLabelKey(key);
						}
						label.setLabel(value);
						labelManager.update(label, false);
					} else {
						labelManager.delete(label.getId());
					}
				}
				languageManager.increaseVersion(id);
				return true;
			} else {
				return false;
			}
	    }
	
		@Override
		protected Logger getLogger() {
			return log;
		}
	}
	
	@Override
	protected Logger getLogger() {
		return log;
	}
}
