package it.samvise85.bookshelf.manager.analytics;

import it.samvise85.bookshelf.model.analytics.Route;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;
import it.samvise85.bookshelf.persist.inmemory.analytics.RouteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RouteManagerImpl extends InMemoryPersistenceUnit<Route> implements RouteManager {
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
