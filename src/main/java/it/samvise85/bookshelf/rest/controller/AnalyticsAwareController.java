package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.exception.BookshelfException;
import it.samvise85.bookshelf.manager.RestErrorManager;
import it.samvise85.bookshelf.manager.RestRequestManager;
import it.samvise85.bookshelf.model.RestRequest;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

public abstract class AnalyticsAwareController extends AbstractController {

	protected <T> T executeMethod (HttpServletRequest request, String methodName) {
		return executeMethod(request, methodName, null, null, null);
	}

	protected <T> T executeMethod (HttpServletRequest request, String methodName, Class<?>[] parameterTypes, Object[] args) {
		return executeMethod(request, methodName, parameterTypes, args, null);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T executeMethod (HttpServletRequest request, String methodName, Class<?>[] parameterTypes, Object[] args, Object requestBody) {
		logMethodStart(methodName, args);
		mailSender.setRequestApp(getRequestApp(request));
		
		RestRequest restRequest = getRequestManager().create(request, methodName, requestBody);
        try {
        	Method method = null;
        	if(parameterTypes != null)
        		method = this.getClass().getDeclaredMethod(methodName, parameterTypes);
        	else
        		method = this.getClass().getDeclaredMethod(methodName);
        	method.setAccessible(true);
        	return (T) method.invoke(this, args);
        } catch(Throwable e) {
        	getErrorManager().create(e, restRequest.getId());
        	if(!(e instanceof BookshelfException))
        		e = new BookshelfException(e.getMessage(), e);
        	throw (BookshelfException)e;
        }
    }

	protected abstract RestRequestManager getRequestManager();
	protected abstract RestErrorManager getErrorManager();

}
