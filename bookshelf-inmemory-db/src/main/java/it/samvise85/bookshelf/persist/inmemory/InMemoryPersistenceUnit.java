package it.samvise85.bookshelf.persist.inmemory;

import it.samvise85.bookshelf.model.Identifiable;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.PersistenceUnit;
import it.samvise85.bookshelf.persist.clauses.Order;
import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.PaginationClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

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

	@Override
	public List<T> getList(PersistOptions options) {
		Pageable pageable = createPageable(options != null ? options.getPagination() : null);
		Iterable<T> all = null;
		if(pageable != null)
			all = getRepository().findAll(); //TODO add selection and order
		else
			all = getRepository().findAll(pageable);
		
		return convertToList(all, options != null ? options.getProjection() : null);
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

	public abstract PagingAndSortingRepository<T, String> getRepository();
	
	@SuppressWarnings("unchecked")
	protected List<T> convertToList(Iterable<T> iterable, ProjectionClause projectionClause) {
		List<T> res = new ArrayList<T>();
		Iterator<T> iterator = iterable.iterator();
		T previous = null;
		while(iterator.hasNext()) {
			T curr = iterator.next();
			if(previous == null || previous != curr) {
				res.add((T) curr.setProjection(projectionClause));
				previous = curr;
			} else break;
		}
		return res;
	}
	@SuppressWarnings("unchecked")
	protected List<T> convertToList(Page<T> page, ProjectionClause projectionClause) {
		List<T> res = new ArrayList<T>();
		for(T curr : page)
			res.add((T) curr.setProjection(projectionClause));
		return res;
	}
	
	protected Pageable createPageable(PaginationClause pagination) {
		if(pagination == null) return null;
		return new PageRequest(pagination.getPage()-1, pagination.getPageSize());
	}
}
