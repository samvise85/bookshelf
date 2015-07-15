package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.Chapter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChapterRepository extends SearchRepository<Chapter, String> {
 
	public Iterable<Chapter> findByBookOrderByPositionAsc(String book);

	public Page<Chapter> findByBookOrderByPositionAsc(String book, Pageable page);

	public Chapter findFirstByBookAndPosition(String book, Integer position);

	public Chapter findFirstByBookOrderByPositionAsc(String book);

	public Chapter findFirstByBookOrderByPositionDesc(String book);

}