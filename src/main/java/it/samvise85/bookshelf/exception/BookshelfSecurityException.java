package it.samvise85.bookshelf.exception;

public class BookshelfSecurityException extends BookshelfException {
	private static final long serialVersionUID = -2497537904234674472L;

	public BookshelfSecurityException() {
		super();
	}

	public BookshelfSecurityException(String arg0, Throwable arg1,
			boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public BookshelfSecurityException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public BookshelfSecurityException(String arg0) {
		super(arg0);
	}

	public BookshelfSecurityException(Throwable arg0) {
		super(arg0);
	}

}
