package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.Route;
import it.samvise85.bookshelf.persist.AbstractPersistenceUnit;
import it.samvise85.bookshelf.persist.repository.RouteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RouteManagerImpl extends AbstractPersistenceUnit<Route> implements RouteManager {
	@Autowired
	protected RouteRepository repository;
	
	public RouteManagerImpl() {
		super(Route.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RouteRepository getRepository() {
		return repository;
	}

}
