package it.samvise85.bookshelf.persist.inmemory;

import it.samvise85.bookshelf.model.Identifiable;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.PersistenceUnit;
import it.samvise85.bookshelf.persist.clauses.Order;
import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.data.repository.CrudRepository;

public abstract class InMemoryPersistenceUnit<T extends Identifiable> implements PersistenceUnit<T> {
	protected Class<T> registeredClass;
	protected static final List<OrderClause> DEFAULT_ORDER = Collections.singletonList(new OrderClause("id", Order.ASC));
	protected static final Logger log = Logger.getLogger(InMemoryPersistenceUnit.class);
	
	public InMemoryPersistenceUnit(Class<T> clazz) {
		registerClass(clazz);
	}

	private void registerClass(Class<T> clazz) {
		this.registeredClass = clazz;
	}

	public T get(String id) {
		return getRepository().findOne(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(String id, ProjectionClause projection) {
		return (T) getRepository().findOne(id).setProjection(projection);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getList(PersistOptions options) {
		Iterable<T> all = getRepository().findAll(); //TODO add selection and order
		List<T> res = new ArrayList<T>();
		Iterator<T> iterator = all.iterator();
		while(iterator.hasNext())
			res.add((T) iterator.next().setProjection(options != null ? options.getProjection() : null));
		return res;
	}

	public T create(T objectToSave) {
		return getRepository().save(objectToSave);
	}

	public T update(T objectToUpdate) {
		return getRepository().save(objectToUpdate);
	}

	public T delete(String id) {
		getRepository().delete(id);
		return null;
	}

	public abstract CrudRepository<T, String> getRepository();
}
