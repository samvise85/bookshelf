package it.samvise85.bookshelf.persist.inmemory.analytics;

import it.samvise85.bookshelf.model.analytics.Route;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface RouteRepository extends PagingAndSortingRepository<Route, Long> {
 
}