package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.Stream;
import it.samvise85.bookshelf.persist.PersistenceUnit;

public interface StreamManager extends PersistenceUnit<Stream> {

	Stream create();

}
