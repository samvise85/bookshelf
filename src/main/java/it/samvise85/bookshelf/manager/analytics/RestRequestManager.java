package it.samvise85.bookshelf.manager.analytics;

import javax.servlet.http.HttpServletRequest;

import it.samvise85.bookshelf.model.analytics.RestRequest;
import it.samvise85.bookshelf.persist.PersistenceUnit;

public interface RestRequestManager extends PersistenceUnit<RestRequest> {

	RestRequest create(HttpServletRequest context, String methodName);

	<T> RestRequest create(HttpServletRequest context, String methodName, T requestBody);

}
