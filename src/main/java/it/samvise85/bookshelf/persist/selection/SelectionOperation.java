package it.samvise85.bookshelf.persist.selection;

import it.samvise85.bookshelf.model.StringIdentifiable;

public interface SelectionOperation {
	public boolean select(StringIdentifiable object, String field, Object value);
}
