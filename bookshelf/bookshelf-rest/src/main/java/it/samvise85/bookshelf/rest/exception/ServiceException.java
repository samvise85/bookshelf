package it.samvise85.bookshelf.rest.exception;

import it.samvise85.bookshelf.exception.BookshelfException;

public class ServiceException extends BookshelfException {
	private static final long serialVersionUID = 8308939339935990994L;

	public ServiceException() {
		super();
	}

	public ServiceException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public ServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ServiceException(String arg0) {
		super(arg0);
	}

	public ServiceException(Throwable arg0) {
		super(arg0);
	}

}