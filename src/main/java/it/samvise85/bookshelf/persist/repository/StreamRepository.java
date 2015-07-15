package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.Stream;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface StreamRepository extends PagingAndSortingRepository<Stream, String> {

}