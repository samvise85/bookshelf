package it.samvise85.bookshelf.manager.analytics;

import it.samvise85.bookshelf.exception.BookshelfException;
import it.samvise85.bookshelf.model.analytics.RestRequest;
import it.samvise85.bookshelf.persist.database.analytics.RestRequestRepository;
import it.samvise85.bookshelf.persist.repository.DatabasePersistenceUnit;
import it.samvise85.bookshelf.rest.security.config.SpringSecurityConfig;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RestRequestManagerImpl extends DatabasePersistenceUnit<RestRequest> implements RestRequestManager {
	@Autowired
	protected RestRequestRepository repository;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public RestRequestManagerImpl() {
		super(RestRequest.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RestRequestRepository getRepository() {
		return repository;
	}

	@Override
	public RestRequest create(HttpServletRequest context, String methodName) {
		return create(context, methodName, null);
	}
	
	@Override
	public <T> RestRequest create(HttpServletRequest context, String methodName, T requestBody) {
		RestRequest request = new RestRequest();
		request.setDate(new Date());
		request.setIp(context.getRemoteAddr().toString());
		request.setRequestURL(getFullURL(context));
		request.setHttpMethod(context.getMethod());
		request.setMethod(methodName);
		request.setUsername(context.getHeader(SpringSecurityConfig.USERNAME_PARAM_NAME));
		request.setLocale(context.getLocale().toString());
		if(requestBody != null) {
			request.setObjectClass(requestBody.getClass().getSimpleName());
			try {
				String objectJson = mapper.writeValueAsString(requestBody);
				request.setObjectJson(objectJson);
			} catch(JsonProcessingException e) {
				throw new BookshelfException(e.getMessage(), e);
			}
		}
		
		return super.create(request);
	}
	
	private static String getFullURL(HttpServletRequest request) {
	    StringBuffer requestURL = request.getRequestURL();
	    String queryString = request.getQueryString();

	    if (queryString == null) {
	        return requestURL.toString();
	    } else {
	        return requestURL.append('?').append(queryString).toString();
	    }
	}
}
