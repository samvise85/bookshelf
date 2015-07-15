package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.Moderation;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface ModerationRepository extends PagingAndSortingRepository<Moderation, String> {
 
}