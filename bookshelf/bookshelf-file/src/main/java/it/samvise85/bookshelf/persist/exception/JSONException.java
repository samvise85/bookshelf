package it.samvise85.bookshelf.persist.exception;

public class JSONException extends PersistException {
	private static final long serialVersionUID = -4953861314566341916L;

	public JSONException() {
		super();
	}

	public JSONException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public JSONException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public JSONException(String arg0) {
		super(arg0);
	}

	public JSONException(Throwable arg0) {
		super(arg0);
	}

}
