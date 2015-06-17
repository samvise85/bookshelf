package it.samvise85.bookshelf.persist.inmemory;

import it.samvise85.bookshelf.model.comment.Stream;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface StreamRepository extends PagingAndSortingRepository<Stream, String> {

}