package it.samvise85.bookshelf.persist.database.support;

import it.samvise85.bookshelf.model.locale.Language;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface LanguageRepository extends PagingAndSortingRepository<Language, String> {

	Language findOneByDef(Boolean def);

}