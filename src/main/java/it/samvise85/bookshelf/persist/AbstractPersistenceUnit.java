package it.samvise85.bookshelf.persist;

import it.samvise85.bookshelf.model.commons.GenericIdentifiable;
import it.samvise85.bookshelf.model.commons.Projectable;
import it.samvise85.bookshelf.persist.clauses.Order;
import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.PaginationClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.repository.SearchRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

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
		return getRepository().findOne(id);
	}

	@Override
	public T get(Serializable id, ProjectionClause projection) {
		try {
			T entity = getRepository().findOne(id);
			if(entity instanceof Projectable)
				if(entity != null) ((Projectable) entity).setProjection(projection);
			return entity;
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<T> getList(PersistOptions options) {
		Pageable pageable = createPageable(options != null ? options.getPagination() : null);
		Iterable<T> all = null;
		if(pageable == null)
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

	public T delete(Serializable id) {
		getRepository().delete(id);
		return null;
	}

	public abstract <S extends Serializable> SearchRepository<T, S> getRepository();
	
	protected List<T> convertToList(Iterable<T> iterable, ProjectionClause projectionClause) {
		List<T> res = new ArrayList<T>();
		Iterator<T> iterator = iterable.iterator();
		T previous = null;
		while(iterator.hasNext()) {
			T curr = iterator.next();
			if(previous == null || previous != curr) {
				if(curr instanceof Projectable)
					((Projectable)curr).setProjection(projectionClause);
				res.add((T) curr);
				previous = curr;
			} else break;
		}
		return res;
	}
	protected List<T> convertToList(Page<T> page, ProjectionClause projectionClause) {
		List<T> res = new ArrayList<T>();
		for(T curr : page) {
			if(curr instanceof Projectable)
				((Projectable)curr).setProjection(projectionClause);
			res.add((T) curr);
		}
		return res;
	}
	
	protected Pageable createPageable(PersistOptions options) {
		return createPageable(options.getPagination(), options.getOrder());
	}
	
	protected Pageable createPageable(PaginationClause pagination) {
		return createPageable(pagination, null);
	}
	
	protected Pageable createPageable(PaginationClause pagination, List<OrderClause> orders) {
		if(pagination == null) return null;
		Sort sort = createSort(orders);
		if(sort == null)
			return new PageRequest(pagination.getPage()-1, pagination.getPageSize());
		return new PageRequest(pagination.getPage()-1, pagination.getPageSize(), sort);
	}
	
	protected Sort createSort(List<OrderClause> orders) {
		if(orders == null) return null;
		List<Sort.Order> o = toOrder(orders);
		return new Sort(o);
	}
	
	protected static List<Sort.Order> toOrder(List<OrderClause> orders) {
		if(orders == null) return null;
		List<Sort.Order> res = new ArrayList<Sort.Order>();
		
		for(OrderClause o : orders)
			res.add(new Sort.Order(o.getOrder() == Order.ASC ? Direction.ASC : Direction.DESC, o.getField()));
		
		return res;
	}

}
