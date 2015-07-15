package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.RestError;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface RestErrorRepository extends PagingAndSortingRepository<RestError, Long> {

	RestError findOneByRestRequest(Long restRequest);
 
}