package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.comment.Moderation;
import it.samvise85.bookshelf.persist.file.FilePersistenceUnit;

import org.springframework.stereotype.Service;

@Service
public class ModerationManagerImpl extends FilePersistenceUnit<Moderation> implements ModerationManager {

	public ModerationManagerImpl() {
		super(Moderation.class);
	}

}
