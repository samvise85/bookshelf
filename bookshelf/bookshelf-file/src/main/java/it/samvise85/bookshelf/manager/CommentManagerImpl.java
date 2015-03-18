package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.comment.Comment;
import it.samvise85.bookshelf.persist.file.FilePersistenceUnit;

import org.springframework.stereotype.Service;

@Service
public class CommentManagerImpl extends FilePersistenceUnit<Comment> implements CommentManager {

	public CommentManagerImpl() {
		super(Comment.class);
	}

}
