package it.samvise85.bookshelf.rest.controller;

import it.samvise85.bookshelf.mail.BookshelfMailSender;
import it.samvise85.bookshelf.model.dto.ResponseDto;
import it.samvise85.bookshelf.persist.clauses.SelectionClause;
import it.samvise85.bookshelf.persist.clauses.SelectionOperation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractController {

	@Autowired
	protected BookshelfMailSender mailSender;

	public AbstractController() {
		super();
	}
	
	protected String getMethodName() {
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		if(ste.length >= 3)
			return ste[2].getMethodName();
		return null;
	}
	
	protected ResponseDto executeMethod (HttpServletRequest request, String methodName) {
		return executeMethod(request, methodName, null, null);
	}
	
	protected ResponseDto executeMethod (HttpServletRequest request, String methodName, Class<?>[] parameterTypes, Object[] args) {
		initExecuteMethod(request, methodName, args);
        try {
        	return invokeMethod(methodName, parameterTypes, args);
        } catch(Throwable e) {
        	return manageError(e);
        }
	}

	private void initExecuteMethod(HttpServletRequest request, String methodName, Object[] args) {
		logMethodStart(methodName, args);
		mailSender.setRequestApp(getRequestApp(request));
	}

	protected ResponseDto invokeMethod(String methodName, Class<?>[] parameterTypes, Object[] args) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		Method method = null;
		if(parameterTypes != null)
			method = this.getClass().getDeclaredMethod(methodName, parameterTypes);
		else
			method = this.getClass().getDeclaredMethod(methodName);
		method.setAccessible(true);
		
		ResponseDto res = new ResponseDto();
		res.setResponse(method.invoke(this, args));
		return res;
	}

	protected ResponseDto manageError(Throwable e) {
		getLogger().error(e);
		ResponseDto res = new ResponseDto();
		res.setError(e.getCause().getMessage());
		return res;
	}

	protected void logMethodStart(String methodName, Object[] args) {
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

	protected String getRequestApp(HttpServletRequest request) {
		String requestUrl = request.getRequestURL().toString();
		Pattern p = Pattern.compile("(http[s]?:\\/\\/[^\\/]+(\\/bookshelf)?)");
	
		Matcher m = p.matcher(requestUrl);
		String requestApp = null;
		if(m.find())
			requestApp = m.group(1);
		return requestApp;
	}

	protected List<SelectionClause> getSelectionFromParameterMap(Map<String, String[]> queryParams) {
		List<SelectionClause> selection = new ArrayList<SelectionClause>();
		
		if(queryParams != null) {
			for(String key : queryParams.keySet()) {
				String[] params = queryParams.get(key);
				String param = null;
				if(params.length >= 1) param = params[0];
				if(param != null)
					selection.add(new SelectionClause(key, SelectionOperation.EQUALS, param));
			}
		}
		return selection;
	}

	protected String decodeParam(String string) {
		java.nio.charset.Charset utf8charset = java.nio.charset.Charset.forName("UTF-8");
		java.nio.charset.Charset iso88591charset = java.nio.charset.Charset.forName("ISO-8859-1");
	
		java.nio.ByteBuffer inputBuffer = java.nio.ByteBuffer.wrap(string.getBytes());
	
		// decode UTF-8
		java.nio.CharBuffer data = utf8charset.decode(inputBuffer);
	
		// encode ISO-8559-1
		java.nio.ByteBuffer outputBuffer = iso88591charset.encode(data);
		byte[] outputData = outputBuffer.array();
		return new String(outputData);
	}

	protected abstract Logger getLogger();

}