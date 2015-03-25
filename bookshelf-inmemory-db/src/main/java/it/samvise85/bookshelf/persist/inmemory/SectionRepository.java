package it.samvise85.bookshelf.persist.inmemory;

import it.samvise85.bookshelf.model.book.Section;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SectionRepository extends PagingAndSortingRepository<Section, String> {
 
    public Page<Section> findByBookOrderByPositionAsc(String book, Pageable page);
}