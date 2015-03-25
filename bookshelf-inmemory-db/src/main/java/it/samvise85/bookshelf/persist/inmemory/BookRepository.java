package it.samvise85.bookshelf.persist.inmemory;

import it.samvise85.bookshelf.model.book.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BookRepository extends PagingAndSortingRepository<Book, String> {
 
    public Page<Book> findByAuthorOrderByTitleAsc(String author, Pageable page);
//    public Page<Book> findAllOrderByTitleAsc(Pageable page);
}