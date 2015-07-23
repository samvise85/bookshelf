package it.samvise85.bookshelf.persist;

import it.samvise85.bookshelf.model.commons.GenericIdentifiable;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

import java.io.Serializable;
import java.util.List;

public interface PersistenceUnit<T extends GenericIdentifiable<?>> {
	
	public T get(Serializable id);

	public T get(Serializable id, ProjectionClause projection);

	public T getOne(PersistOptions options);
	
	public List<T> getList(PersistOptions options);
	
	public T create(T objectToSave);
	
	public T update(T objectToUpdate);
	
	public T delete(Serializable id);

	
}
