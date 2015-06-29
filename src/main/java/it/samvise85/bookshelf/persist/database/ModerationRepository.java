package it.samvise85.bookshelf.persist.database;

import it.samvise85.bookshelf.model.comment.Moderation;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface ModerationRepository extends PagingAndSortingRepository<Moderation, String> {
 
}