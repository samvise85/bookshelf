package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.Language;

public interface LanguageRepository extends SearchRepository<Language, String> {

	Language findOneByDef(Boolean def);

}