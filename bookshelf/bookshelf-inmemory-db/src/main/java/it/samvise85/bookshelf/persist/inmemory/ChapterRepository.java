package it.samvise85.bookshelf.persist.inmemory;

import it.samvise85.bookshelf.model.book.Chapter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ChapterRepository extends PagingAndSortingRepository<Chapter, String> {
 
    public Page<Chapter> findByBookOrderByPositionAsc(String book, Pageable page);
}