package it.samvise85.bookshelf.persist.selection;

import it.samvise85.bookshelf.model.Identifiable;

public class NotEquals implements SelectionOperation {
	private static final NotEquals instance = new NotEquals();
	
	private NotEquals() {}

	public static NotEquals getInstance() {
		return instance;
	}

	@Override
	public boolean select(Identifiable object, String field, Object value) {
		return !Equals.getInstance().select(object, field, value);
	}

}
