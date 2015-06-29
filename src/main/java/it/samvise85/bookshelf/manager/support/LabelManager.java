package it.samvise85.bookshelf.manager.support;

import it.samvise85.bookshelf.model.locale.Label;
import it.samvise85.bookshelf.persist.PersistenceUnit;

public interface LabelManager extends PersistenceUnit<Label> {

	Label get(String key, String language);

	Label getDefault(String key);

	void deleteAll();

}
