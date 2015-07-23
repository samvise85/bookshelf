package it.samvise85.bookshelf.persist;

import it.samvise85.bookshelf.model.commons.GenericIdentifiable;
import it.samvise85.bookshelf.persist.clauses.Order;
import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.clauses.SelectionClause;
import it.samvise85.bookshelf.persist.clauses.SelectionOperation;
import it.samvise85.bookshelf.persist.repository.SearchRepository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class AbstractPersistenceUnit<T extends GenericIdentifiable<?>> implements PersistenceUnit<T> {
	protected Class<T> registeredClass;
	protected static final List<OrderClause> DEFAULT_ORDER = Collections.singletonList(new OrderClause("id", Order.ASC));
	protected static final Logger log = Logger.getLogger(AbstractPersistenceUnit.class);
	
	public AbstractPersistenceUnit(Class<T> clazz) {
		registerClass(clazz);
	}

	private void registerClass(Class<T> clazz) {
		this.registeredClass = clazz;
	}

	public T get(Serializable id) {
		return get(id, null);
	}

	@Override
	public T get(Serializable id, ProjectionClause projection) {
		return getOne(new PersistOptions(projection, Collections.singletonList(new SelectionClause("id", SelectionOperation.EQUALS, id))));
	}

	@Override
	public T getOne(PersistOptions options) {
		return getRepository().searchOne(options);
	}

	@Override
	public List<T> getList(PersistOptions options) {
		return getRepository().search(options);
	}

	public T create(T objectToSave) {
		return getRepository().save(objectToSave);
	}

	public T update(T objectToUpdate) {
		return getRepository().save(objectToUpdate);
	}

	public T delete(Serializable id) {
		getRepository().delete(id);
		return null;
	}

	public abstract <S extends Serializable> SearchRepository<T, S> getRepository();
	
}
