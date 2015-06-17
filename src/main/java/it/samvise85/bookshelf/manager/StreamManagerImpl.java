package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.comment.Stream;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;
import it.samvise85.bookshelf.persist.inmemory.StreamRepository;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StreamManagerImpl extends InMemoryPersistenceUnit<Stream> implements StreamManager {
	@Autowired
	private StreamRepository repository;
	
	public StreamManagerImpl() {
		super(Stream.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public StreamRepository getRepository() {
		return repository;
	}

	@Override
	public Stream create() {
		Stream st = new Stream();
		st.setId("" + new Date().getTime());
		return super.create(st);
	}

}
