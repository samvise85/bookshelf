package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.Language;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface LanguageRepository extends PagingAndSortingRepository<Language, String> {

	Language findOneByDef(Boolean def);

}