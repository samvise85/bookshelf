package it.samvise85.bookshelf.persist.selection;

import it.samvise85.bookshelf.model.Identifiable;
import it.samvise85.bookshelf.persist.exception.PersistException;

import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

public class IsNull implements SelectionOperation {
	private static final IsNull instance = new IsNull();
	
	private IsNull() {}

	public static IsNull getInstance() {
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
		return f1 == null;
	}

}
