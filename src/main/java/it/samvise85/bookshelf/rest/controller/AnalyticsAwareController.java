package it.samvise85.bookshelf.rest.controller;

import java.lang.reflect.Method;

import it.samvise85.bookshelf.exception.BookshelfException;
import it.samvise85.bookshelf.manager.analytics.RestErrorManager;
import it.samvise85.bookshelf.manager.analytics.RestRequestManager;
import it.samvise85.bookshelf.model.analytics.RestRequest;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public abstract class AnalyticsAwareController {
	
	protected <T> T executeMethod (HttpServletRequest request, String methodName) {
		return executeMethod(request, methodName, null, null, null);
	}
	
	protected <T> T executeMethod (HttpServletRequest request, String methodName, Class<?>[] parameterTypes, Object[] args) {
		return executeMethod(request, methodName, parameterTypes, args, null);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T executeMethod (HttpServletRequest request, String methodName, Class<?>[] parameterTypes, Object[] args, Object requestBody) {
		logMethodStart(methodName, args);
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

	private void logMethodStart(String methodName, Object[] args) {
		StringBuilder sb = new StringBuilder(methodName);
		if(args != null) {
			for(int i = 0; i < args.length; i++) {
				if(i > 0) sb.append("; ");
				sb.append("arg");
				sb.append(i);
				sb.append(": ");
				sb.append(args[i] != null ? args[i].toString() : "null");
			}
		}
		getLogger().info(sb.toString());
	}
	
	protected abstract RestRequestManager getRequestManager();
	protected abstract RestErrorManager getErrorManager();
	protected abstract Logger getLogger();

}
