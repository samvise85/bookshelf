package it.samvise85.bookshelf.persist.database;

import it.samvise85.bookshelf.model.book.Chapter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ChapterRepository extends PagingAndSortingRepository<Chapter, String> {
 
	public Iterable<Chapter> findByBookOrderByPositionAsc(String book);

	public Page<Chapter> findByBookOrderByPositionAsc(String book, Pageable page);

	public Chapter findFirstByBookAndPosition(String book, Integer position);

	public Chapter findFirstByBookOrderByPositionAsc(String book);

	public Chapter findFirstByBookOrderByPositionDesc(String book);

}