package it.samvise85.bookshelf.manager.analytics;

import it.samvise85.bookshelf.model.analytics.RestError;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;
import it.samvise85.bookshelf.persist.inmemory.analytics.RestErrorRepository;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestErrorManagerImpl extends InMemoryPersistenceUnit<RestError> implements RestErrorManager {
	@Autowired
	protected RestErrorRepository repository;
	
	public RestErrorManagerImpl() {
		super(RestError.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RestErrorRepository getRepository() {
		return repository;
	}

	@Override
	public RestError create(Throwable ex, Long requestId) {
		RestError error = new RestError();
		error.setMessage(ex.getClass().getCanonicalName() + ": " + ex.getMessage());
		StringWriter trace = new StringWriter();
		ex.printStackTrace(new PrintWriter(trace));
		
		String stackTrace = trace.toString();
		error.setStackTrace(stackTrace);
		error.setRestRequest(requestId);
		
		return super.create(error);
	}

	@Override
	public RestError getRequestError(Long requestId) {
		return repository.findOneByRestRequest(requestId);
	}

}
