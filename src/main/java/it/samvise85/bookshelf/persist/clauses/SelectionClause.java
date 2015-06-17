package it.samvise85.bookshelf.persist.clauses;

import it.samvise85.bookshelf.model.StringIdentifiable;
import it.samvise85.bookshelf.persist.exception.PersistException;
import it.samvise85.bookshelf.persist.selection.SelectionOperation;

public class SelectionClause {
	private String field;
	private SelectionOperation operation;
	private Object value;
	
	public SelectionClause(String field, SelectionOperation operation, Object value) {
		super();
		this.field = field;
		this.operation = operation;
		this.value = value;
	}

	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public SelectionOperation getOperation() {
		return operation;
	}
	public void setOperation(SelectionOperation operation) {
		this.operation = operation;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}

	public boolean select(StringIdentifiable object) throws PersistException {
		return operation.select(object, field, value);
	}
}
