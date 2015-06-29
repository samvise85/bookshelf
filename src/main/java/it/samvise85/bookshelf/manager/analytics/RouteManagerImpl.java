package it.samvise85.bookshelf.manager.analytics;

import it.samvise85.bookshelf.model.analytics.Route;
import it.samvise85.bookshelf.persist.database.DatabasePersistenceUnit;
import it.samvise85.bookshelf.persist.database.analytics.RouteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RouteManagerImpl extends DatabasePersistenceUnit<Route> implements RouteManager {
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
