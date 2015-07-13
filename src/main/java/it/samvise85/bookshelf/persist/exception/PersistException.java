package it.samvise85.bookshelf.persist.exception;

import it.samvise85.bookshelf.exception.BookshelfException;

public class PersistException extends BookshelfException {
	private static final long serialVersionUID = 5796870983098008187L;

	public PersistException() {
		super();
	}

	public PersistException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public PersistException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public PersistException(String arg0) {
		super(arg0);
	}

	public PersistException(Throwable arg0) {
		super(arg0);
	}

}
