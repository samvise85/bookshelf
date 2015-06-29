package it.samvise85.bookshelf.persist.database.analytics;

import it.samvise85.bookshelf.model.analytics.RestRequest;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface RestRequestRepository extends PagingAndSortingRepository<RestRequest, Long> {
	
}