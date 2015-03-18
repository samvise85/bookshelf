package it.samvise85.bookshelf.persist;

import it.samvise85.bookshelf.model.Identifiable;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

import java.util.List;

public interface PersistenceUnit<T extends Identifiable> {
	
	public T get(String id);

	public T get(String id, ProjectionClause projection);
	
	public List<T> getList(PersistOptions options);
	
	public T create(T objectToSave);
	
	public T update(T objectToUpdate);
	
	public T delete(String id);

	
}
