package it.samvise85.bookshelf.persist.clauses;

import it.samvise85.bookshelf.exception.BookshelfException;

import java.lang.reflect.Field;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

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

	public Criterion toRestriction(Class<?> clazz) {
		switch (getOperation()) {
		case EQUALS:
			return Restrictions.eq(getField(), cast(clazz, field, value));
		case NOT_EQUALS:
			return Restrictions.not(Restrictions.eq(getField(), cast(clazz, field, value)));
		case IS_NULL:
			return Restrictions.isNull(field);
		case IS_NOT_NULL:
			Restrictions.isNotNull(field);
		default:
			return null;
		}
	}

	private Object cast(Class<?> clazz, String fieldName, Object value) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			if(value.getClass().isAssignableFrom(field.getType()))
				return value;
			if(value instanceof String) {
				if(StringUtils.isEmpty((String)value)) return null;
				
				if(field.getType().isAssignableFrom(Integer.class))
					return Integer.parseInt((String)value);
				if(field.getType().isAssignableFrom(Double.class))
					return Integer.parseInt((String)value);
				if(field.getType().isAssignableFrom(Long.class))
					return Integer.parseInt((String)value);

				if(field.getType().isAssignableFrom(Date.class)) {
					//TODO
				}
			}
			return value;
		} catch(NoSuchFieldException e) {
			throw new BookshelfException(e.getMessage(), e);
		} catch(Exception e) {
			throw new BookshelfException(e.getMessage(), e);
		}
	}
}
