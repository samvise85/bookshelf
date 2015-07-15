package it.samvise85.bookshelf.manager;

import javax.servlet.http.HttpServletRequest;

import it.samvise85.bookshelf.model.RestRequest;
import it.samvise85.bookshelf.persist.PersistenceUnit;

public interface RestRequestManager extends PersistenceUnit<RestRequest> {

	RestRequest create(HttpServletRequest context, String methodName);

	<T> RestRequest create(HttpServletRequest context, String methodName, T requestBody);

}
