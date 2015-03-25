package it.samvise85.bookshelf.persist.inmemory;

import it.samvise85.bookshelf.model.comment.Comment;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface CommentRepository extends PagingAndSortingRepository<Comment, String> {
 
}