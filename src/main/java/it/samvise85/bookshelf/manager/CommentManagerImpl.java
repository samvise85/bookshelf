package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.exception.BookshelfException;
import it.samvise85.bookshelf.model.comment.Comment;
import it.samvise85.bookshelf.model.comment.Stream;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.NoProjectionClause;
import it.samvise85.bookshelf.persist.clauses.SelectionClause;
import it.samvise85.bookshelf.persist.database.CommentRepository;
import it.samvise85.bookshelf.persist.database.DatabasePersistenceUnit;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentManagerImpl extends DatabasePersistenceUnit<Comment> implements CommentManager {
	@Autowired
	private CommentRepository repository;
	@Autowired
	protected StreamManager streamManager;
	
	public CommentManagerImpl() {
		super(Comment.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommentRepository getRepository() {
		return repository;
	}

	@Override
	public List<Comment> getList(PersistOptions options) {
		String streamId = null;
		if(options.getSelection() != null && !options.getSelection().isEmpty()) {
			for(SelectionClause s : options.getSelection()) {
				if(s.getField().equalsIgnoreCase("parentStream")) {
					streamId = (String) s.getValue();
				}
			}
		}
		if(streamId == null)
			throw new BookshelfException("Stream cannot be null");
		if(options.getPagination() != null)
			return convertToList(repository.findByParentStreamOrderByCreationDesc(streamId, createPageable(options.getPagination())), options.getProjection());
		else
			return convertToList(repository.findByParentStreamOrderByCreationDesc(streamId), options.getProjection());
	}

	@Override
	public Comment create(Comment objectToSave) {
		Date date = new Date();
		if(objectToSave.getCreation() == null) {
			objectToSave.setCreation(date);
		} else {
			date = objectToSave.getCreation();
		}
		objectToSave.setLastModification(date);
		objectToSave.setId(objectToSave.getUser()+date.getTime());
		return addStream(super.create(objectToSave));
	}

	@Override
	public Comment update(Comment updates) {
		Comment commentToUpdate = get(updates.getId(), NoProjectionClause.NO_PROJECTION);
		Date date = new Date();
		if(commentToUpdate.getCreation() == null)
			commentToUpdate.setCreation(date);
		commentToUpdate.setLastModification(date);
		commentToUpdate.setComment(updates.getComment());
		return super.update(commentToUpdate);
	}

	@Transactional(value=TxType.REQUIRES_NEW)
	private Comment addStream(Comment comm) {
		if(comm.getStream() == null) {
			Stream stream = streamManager.create();
			comm.setStream(stream.getId());
			super.update(comm);
		}
		return comm;
	}
}
