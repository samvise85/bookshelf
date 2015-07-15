package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.Language;
import it.samvise85.bookshelf.persist.PersistenceUnit;

public interface LanguageManager extends PersistenceUnit<Language> {

	Language getDefault();

	Language changeDefault(String language);

	Language increaseVersion(String lang);

}
