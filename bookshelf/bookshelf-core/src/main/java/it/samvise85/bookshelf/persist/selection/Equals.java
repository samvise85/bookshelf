package it.samvise85.bookshelf.persist.selection;

import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

import it.samvise85.bookshelf.model.Identifiable;
import it.samvise85.bookshelf.persist.exception.PersistException;

public class Equals implements SelectionOperation {
	private static final Equals instance = new Equals();
	
	private Equals() {}

	public static Equals getInstance() {
		return instance;
	}

	@Override
	public boolean select(Identifiable object, String field, Object value) {
		Object f1 = null;
		try {
			Field declaredField = object.getClass().getDeclaredField(field);
			ReflectionUtils.makeAccessible(declaredField);
			f1 = ReflectionUtils.getField(declaredField, object);
		} catch (Exception e) {
			throw new PersistException(e.getMessage(), e);
		}
		if(f1 == null || value == null) return false;
		if(f1.getClass().isInstance(value) || (f1 instanceof Number && value instanceof Number)) {
			return f1.equals(value);
		} else {
			throw new PersistException("Field is a " + f1.getClass().getSimpleName() + " while value is a " + value.getClass().getSimpleName());
		}
	}

}
