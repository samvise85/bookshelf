package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.comment.Stream;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;
import it.samvise85.bookshelf.persist.inmemory.StreamRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StreamManagerImpl extends InMemoryPersistenceUnit<Stream> implements StreamManager {
	@Autowired
	private StreamRepository repository;
	
	public StreamManagerImpl() {
		super(Stream.class);
	}

	@Override
	public StreamRepository getRepository() {
		return repository;
	}

}
