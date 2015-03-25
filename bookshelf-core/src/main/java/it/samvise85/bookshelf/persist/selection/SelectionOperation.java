package it.samvise85.bookshelf.persist.selection;

import it.samvise85.bookshelf.model.Identifiable;

public interface SelectionOperation {
	public boolean select(Identifiable object, String field, Object value);
}
