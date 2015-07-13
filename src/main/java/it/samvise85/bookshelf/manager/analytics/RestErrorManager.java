package it.samvise85.bookshelf.manager.analytics;

import it.samvise85.bookshelf.model.analytics.RestError;
import it.samvise85.bookshelf.persist.PersistenceUnit;

public interface RestErrorManager extends PersistenceUnit<RestError> {

	RestError create(Throwable error, Long requestId);

	RestError getRequestError(Long requestId);

}
