package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.comment.Comment;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentManagerImpl extends InMemoryPersistenceUnit<Comment> implements CommentManager {

	public CommentManagerImpl() {
		super(Comment.class);
	}

	@Override
	public CrudRepository<Comment, String> getRepository() {
		// TODO Auto-generated method stub
		return null;
	}

}
