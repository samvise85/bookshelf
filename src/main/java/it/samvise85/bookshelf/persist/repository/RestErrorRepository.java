package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.RestError;

public interface RestErrorRepository extends SearchRepository<RestError, Long> {

	RestError findOneByRestRequest(Long restRequest);
 
}