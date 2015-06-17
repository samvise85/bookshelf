package it.samvise85.bookshelf.persist.selection;

import it.samvise85.bookshelf.model.StringIdentifiable;

public class IsNotNull implements SelectionOperation {
	private static final IsNotNull instance = new IsNotNull();
	
	private IsNotNull() {}

	public static IsNotNull getInstance() {
		return instance;
	}

	@Override
	public boolean select(StringIdentifiable object, String field, Object value) {
		return !IsNull.getInstance().select(object, field, value);
	}

}
