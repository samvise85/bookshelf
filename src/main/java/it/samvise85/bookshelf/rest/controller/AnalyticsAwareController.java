package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.manager.RestErrorManager;
import it.samvise85.bookshelf.manager.RestRequestManager;
import it.samvise85.bookshelf.model.RestRequest;
import it.samvise85.bookshelf.model.dto.ResponseDto;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class AnalyticsAwareController extends AbstractController {

	@Autowired
	RestRequestManager requestManager;
	@Autowired
	RestErrorManager errorManager;
	
	@Override
	protected ResponseDto executeMethod (HttpServletRequest request, String methodName) {
		return executeMethod(request, methodName, null, null, null);
	}

	@Override
	protected ResponseDto executeMethod (HttpServletRequest request, String methodName, Class<?>[] parameterTypes, Object[] args) {
		return executeMethod(request, methodName, parameterTypes, args, null);
	}
	
	protected ResponseDto executeMethod (HttpServletRequest request, String methodName, Class<?>[] parameterTypes, Object[] args, Object requestBody) {
		RestRequest restRequest = getRequestManager().create(request, methodName, requestBody);
        try {
        	return invokeMethod(methodName, parameterTypes, args);
        } catch(Throwable e) {
        	return manageError(e, restRequest);
        }
    }

	protected ResponseDto manageError(Throwable e, RestRequest restRequest) {
    	getErrorManager().create(e, restRequest.getId());
		return super.manageError(e);
	}

	protected abstract RestRequestManager getRequestManager();
	protected abstract RestErrorManager getErrorManager();

}
