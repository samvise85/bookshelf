package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.RestRequest;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface RestRequestRepository extends PagingAndSortingRepository<RestRequest, Long> {
	
}