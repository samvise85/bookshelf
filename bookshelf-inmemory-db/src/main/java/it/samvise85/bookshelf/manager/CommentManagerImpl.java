package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.comment.Comment;
import it.samvise85.bookshelf.persist.inmemory.CommentRepository;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentManagerImpl extends InMemoryPersistenceUnit<Comment> implements CommentManager {
	@Autowired
	private CommentRepository repository;
	
	public CommentManagerImpl() {
		super(Comment.class);
	}

	@Override
	public CommentRepository getRepository() {
		return repository;
	}

}
