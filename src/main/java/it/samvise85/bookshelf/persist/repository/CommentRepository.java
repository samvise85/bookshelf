package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepository extends SearchRepository<Comment, String> {

	public Iterable<Comment> findByParentStreamOrderByCreationDesc(String parentStream);

	public Page<Comment> findByParentStreamOrderByCreationDesc(String parentStream, Pageable page);

	public Iterable<Comment> findFirst10ByParentStreamOrderByCreationDesc(String parentStream);

}