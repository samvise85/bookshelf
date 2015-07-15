package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.RestError;
import it.samvise85.bookshelf.persist.AbstractPersistenceUnit;
import it.samvise85.bookshelf.persist.repository.RestErrorRepository;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestErrorManagerImpl extends AbstractPersistenceUnit<RestError> implements RestErrorManager {
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
