package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.Section;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SectionRepository extends SearchRepository<Section, String> {
 
    public Page<Section> findByBookOrderByPositionAsc(String book, Pageable page);
}