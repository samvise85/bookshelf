package it.samvise85.bookshelf.persist.inmemory;

import it.samvise85.bookshelf.model.Identifiable;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.PersistenceUnit;
import it.samvise85.bookshelf.persist.clauses.Order;
import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

public class InMemoryPersistenceUnit<T extends Identifiable> implements PersistenceUnit<T> {
	private Class<T> registeredClass;
	private static final List<OrderClause> DEFAULT_ORDER = Collections.singletonList(new OrderClause("id", Order.ASC));
	
	private static final Logger log = Logger.getLogger(InMemoryPersistenceUnit.class);
	
	public InMemoryPersistenceUnit(Class<T> clazz) {
		registerClass(clazz);
	}

	private void registerClass(Class<T> clazz) {
		this.registeredClass = clazz;
	}

	public T get(String id) {
		//TODO
		return null;
	}

	@Override
	public T get(String id, ProjectionClause projection) {
		//TODO
		return null;
	}

	@Override
	public List<T> getList(PersistOptions options) {
		//TODO
		return null;
	}

	public T create(T objectToSave) {
		//TODO
		return null;
	}

	public T update(T objectToUpdate) {
		//TODO
		return null;
	}

	public T delete(String id) {
		//TODO
		return null;
	}

}
