package it.samvise85.bookshelf.persist.inmemory.analytics;

import it.samvise85.bookshelf.model.analytics.RestError;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface RestErrorRepository extends PagingAndSortingRepository<RestError, Long> {

	RestError findOneByRestRequest(Long restRequest);
 
}