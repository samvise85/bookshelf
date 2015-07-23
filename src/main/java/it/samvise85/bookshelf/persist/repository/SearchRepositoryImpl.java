package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.SelectionClause;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class SearchRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements SearchRepository<T, ID> {

    private EntityManager em;

	public SearchRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
		super(domainClass, entityManager);
		this.em = entityManager;
	}
	  
	@Override
	@SuppressWarnings("unchecked")
	public List<T> search(PersistOptions options) {
		Criteria criteria = em.unwrap(Session.class).createCriteria(getDomainClass());
		
		if(options != null) {
			if(options.getProjection() != null) {
				criteria.setProjection(options.getProjection().toProjection(getDomainClass()));
				criteria.setResultTransformer(options.getProjection().getTransformer(getDomainClass()));
			}
			if(options.getSelection() != null) {
				for(SelectionClause sel : options.getSelection()) {
					Criterion crit = sel.toRestriction(getDomainClass());
					if(crit != null)
						criteria.add(crit);
				}
			}
			if(options.getOrder() != null) {
				for(OrderClause ord : options.getOrder()) {
					criteria.addOrder(ord.toOrder());
				}
			}
			if(options.getPagination() != null) {
				int page = options.getPagination().getPage(); //1 based
				int pageSize = options.getPagination().getPageSize();
				criteria.setFirstResult((page-1)*pageSize);
				criteria.setMaxResults(pageSize);
			}
		}
		return criteria.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T searchOne(PersistOptions options) {
		Criteria criteria = em.unwrap(Session.class).createCriteria(getDomainClass());
		
		if(options.getProjection() != null) {
			criteria.setProjection(options.getProjection().toProjection(getDomainClass()));
			criteria.setResultTransformer(options.getProjection().getTransformer(getDomainClass()));
		}
		if(options.getSelection() != null) {
			for(SelectionClause sel : options.getSelection()) {
				Criterion crit = sel.toRestriction(getDomainClass());
				if(crit != null)
					criteria.add(crit);
			}
		}
		if(options.getOrder() != null) {
			for(OrderClause ord : options.getOrder()) {
				criteria.addOrder(ord.toOrder());
			}
		}
		criteria.setMaxResults(1);
		return (T) criteria.uniqueResult();
	}
}
