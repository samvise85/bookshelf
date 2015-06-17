package it.samvise85.bookshelf.manager.support;

import it.samvise85.bookshelf.model.locale.Language;
import it.samvise85.bookshelf.persist.PersistenceUnit;

public interface LanguageManager extends PersistenceUnit<Language> {

	Language getDefault();

	Language changeDefault(String language);

	Language increaseVersion(String lang);

}
