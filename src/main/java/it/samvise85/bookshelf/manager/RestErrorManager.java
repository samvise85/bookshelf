package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.RestError;
import it.samvise85.bookshelf.persist.PersistenceUnit;

public interface RestErrorManager extends PersistenceUnit<RestError> {

	RestError create(Throwable error, Long requestId);

	RestError getRequestError(Long requestId);

}
