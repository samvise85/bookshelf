package it.samvise85.bookshelf.exception;

public class BookshelfException extends RuntimeException {
	private static final long serialVersionUID = 5796870983098008187L;

	public BookshelfException() {
		super();
	}

	public BookshelfException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public BookshelfException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public BookshelfException(String arg0) {
		super(arg0);
	}

	public BookshelfException(Throwable arg0) {
		super(arg0);
	}

}
