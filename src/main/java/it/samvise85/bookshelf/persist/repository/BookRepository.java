package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.book.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends SearchRepository<Book, String> {
 
    public Page<Book> findByAuthorOrderByTitleAsc(String author, Pageable page);
}