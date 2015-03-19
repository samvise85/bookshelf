package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.comment.Stream;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class StreamManagerImpl extends InMemoryPersistenceUnit<Stream> implements StreamManager {

	public StreamManagerImpl() {
		super(Stream.class);
	}

	@Override
	public CrudRepository<Stream, String> getRepository() {
		// TODO Auto-generated method stub
		return null;
	}

}
